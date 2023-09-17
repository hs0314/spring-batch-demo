package me.heesu.springbatchdemo.job;

import lombok.RequiredArgsConstructor;
import me.heesu.springbatchdemo.core.domain.accounts.Accounts;
import me.heesu.springbatchdemo.core.domain.accounts.AccountsRepository;
import me.heesu.springbatchdemo.core.domain.orders.Orders;
import me.heesu.springbatchdemo.core.domain.orders.OrdersRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

/**
 * 주문(order) -> 정산(account)테이블로 데이터 이관 작업
 * tasklet을 사용하지 않고 item reader, processor, item writer 사용
 */
@Configuration
@RequiredArgsConstructor
public class DataMigrationJobConfig {

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    AccountsRepository accountsRepository;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job dateMigrationJob(Step dataMigrationStep){
        // job -> step

        return jobBuilderFactory.get("dateMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(dataMigrationStep)
                .build();
    }

    @JobScope // job 하위 실행
    @Bean
    public Step dataMigrationStep(ItemReader ordersReader,
                                  ItemProcessor orderToAccountProcessor,
                                  ItemWriter accountsWriter) {
        return stepBuilderFactory.get("dataMigrationStep")
                .<Orders, Accounts>chunk(5) // data commit 단위
                .reader(ordersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(orderToAccountProcessor) // order -> account 가공
                .writer(accountsWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> ordersReader(){
        return new RepositoryItemReaderBuilder<Orders>()
                .name("ordersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5) // 보통 chunk size와 맞춤
                .arguments(Collections.emptyList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> orderToAccountProcessor(){
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders order) throws Exception {
                return new Accounts(order);
            }
        };
    }

    // xxx: 그냥 ItemWriter를 사용해도 무방함
    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> accountsWriter(){
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }
}

package springbatchdemo.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import springbatchdemo.core.domain.accounts.AccountsRepository;
import springbatchdemo.core.domain.orders.Orders;
import springbatchdemo.core.domain.orders.OrdersRepository;
import springbatchdemo.core.domain.accounts.Accounts;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
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
public class E4_DataMigrationJobConfig {

    private final OrdersRepository ordersRepository;

    private final AccountsRepository accountsRepository;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dateMigrationJob(Step dataMigrationStep){
        // job -> step

        return new JobBuilder("dateMigrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dataMigrationStep)
                .build();
    }

    @JobScope // job 하위 실행
    @Bean
    public Step dataMigrationStep(ItemReader ordersReader,
                                  ItemProcessor orderToAccountProcessor,
                                  ItemWriter accountsWriter) {
        return new StepBuilder("dataMigrationStep", jobRepository)
                .<Orders, Accounts>chunk(5, transactionManager) // data commit 단위
                .reader(ordersReader)
                .processor(orderToAccountProcessor) // order -> account 가공
                .writer(accountsWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> ordersReader(){
        RepositoryItemReader<Orders> itemReader =  new RepositoryItemReaderBuilder<Orders>()
                .name("ordersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5) // 보통 chunk size와 맞춤
                .arguments(Collections.emptyList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();

        return itemReader;
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

    // 그냥 ItemWriter를 사용해도 무방함
    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> accountsWriter(){
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }
}

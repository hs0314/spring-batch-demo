package me.heesu.springbatchdemo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbatchdemo.core.domain.accounts.AccountsRepository;
import springbatchdemo.core.domain.orders.Orders;
import springbatchdemo.core.domain.orders.OrdersRepository;
import springbatchdemo.job.E4_DataMigrationJobConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") // h2 db 사용
@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, E4_DataMigrationJobConfig.class})
class DbAccessJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @AfterEach
    public void cleanup(){
        ordersRepository.deleteAll();
        accountsRepository.deleteAll();
    }

    @Test
    public void test1_noData() throws Exception{
        //when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(0, accountsRepository.count());
    }

    @Test
    public void test1_mig() throws Exception{
        //given
        Orders o1 = new Orders(null, "item1", 10000, new Date());
        Orders o2 = new Orders(null, "item2", 5000, new Date());

        ordersRepository.save(o1);
        ordersRepository.save(o2);

        //when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(2, accountsRepository.count());
    }
}
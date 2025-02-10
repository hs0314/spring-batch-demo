package springbatchdemo.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springbatchdemo.TestBatchConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") // h2 db 사용
@SpringBatchTest
@SpringBootTest(classes = {TestBatchConfig.class, E1_HelloWorldJobConfig.class})
class HelloWorldJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void jobExecutionTest() throws Exception{

        //given
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        //when
        // SpringBootTest에 Job 클래스를 넘기면 JobLauncherTestUtils가 자동으로 해당 Job을 주입받음
        // 명시적으로 Job 주입을 원하면 @BeforeEach에서 JobLauncherTestUtils가.setJob()으로 넘겨도 됌
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);

        //then
        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

    @Test
    public void stepExecutionTest() throws Exception{

        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);

        StepExecution stepExecution = (StepExecution)((List)jobExecution.getStepExecutions()).get(0);

        //then
        Assertions.assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
        Assertions.assertEquals("helloWorldStep", stepExecution.getStepName());
    }
}
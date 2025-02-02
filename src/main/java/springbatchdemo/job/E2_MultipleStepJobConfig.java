package springbatchdemo.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import springbatchdemo.listener.JobRepositoryListener;

/**
 * N개의 step을 순차적으로 처리하는 job 예시
 * - step별로 데이터가 공유되어야하면 ExecutionContext을 활용
 */
@Configuration
@RequiredArgsConstructor
public class E2_MultipleStepJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final JobRepositoryListener jobRepositoryListener;

    @Bean
    public Job multipleStepJob(Step multipleStep1,
                               Step multipleStep2,
                               Step multipleStep3){

        return new JobBuilder("multipleStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multipleStep1)
                .next(multipleStep2)
                .next(multipleStep3)
                .listener(jobRepositoryListener)
                .build();
    }

    // 별도의 Tasklet을 반환하는 @StepScope 처리 메서드가 없어도 람다로 단순하게 표현 가능
    @JobScope
    @Bean
    public Step multipleStep1() {
        return new StepBuilder("multipleStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step1");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step multipleStep2() {
        return new StepBuilder("multipleStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2");


                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    executionContext.put("someKey", "hello!!");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step multipleStep3() {
        return new StepBuilder("multipleStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step3");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    System.out.println(executionContext.get("someKey"));

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}

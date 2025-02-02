package springbatchdemo.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import springbatchdemo.validator.FileParamValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 배치잡에 특정 파일명(test.csv)을 파라미터 전달받고 데이터 검증
 * --spring.batch.job.names=validatedParamJob -fileName=test.csv
 */
@Configuration
@RequiredArgsConstructor
public class E3_ValidatedParamJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    // N개의 validator 체이닝을 할 수 있음
    private CompositeJobParametersValidator multipleValidators(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        validator.setValidators(Arrays.asList(new FileParamValidator()));

        return validator;
    }

    @Bean
    public Job validatedParamJob(Step validatedParamStep){
        // job -> step
        
        // CompositeJobParametersValidator를 이용해서 N개의 validator 등록이 가능함

        return new JobBuilder("validatedParamJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .validator(new FileParamValidator()) // spring batch에서는 tasklet 수행 이전에 파라미터 검증을 job builder에서 할 수 있도록 validator 제공
                .start(validatedParamStep)
                .build();
    }

    @JobScope // job 하위 실행
    @Bean
    public Step validatedParamStep(Tasklet validatedParamTasklet) {
        return new StepBuilder("validatedParamStep", jobRepository)
                .tasklet(validatedParamTasklet, transactionManager) // read/write 없는 단순한 구조
                .build();
    }

    // 배치 실행 아규먼트를 @Value 어노테이션을 통해서 받아옴
    @StepScope // step 하위 실행
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("filename : " + fileName);
                System.out.println("validatedParamTasklet done.");
                return RepeatStatus.FINISHED; // 다음 step 없음
            }
        };
    }
}

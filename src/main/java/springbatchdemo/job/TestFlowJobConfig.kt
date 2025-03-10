package springbatchdemo.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class TestFlowJobConfig (
        private val jobRepository: JobRepository,
        private val transactionManager: PlatformTransactionManager
) {

    // Step1의 성공여부에 따른 Step2,3 조건부 실행
    @Bean
    fun testFlowJob(): Job {
        return JobBuilder("TestFlowJob", jobRepository)
                .incrementer(RunIdIncrementer())
                .start(step1())
                .on("COMPLETED").to(step3())
                .from(step1())
                .on("FAILED").to(step2())
                .end()
                .build()
    }

    @JobScope
    @Bean
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    Thread.sleep(1000)
                    println("step1 done")
                    val i = 1/0; // xxx: 실패시 flow 조건에 따라서 step2 실행
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }

    @Bean
    fun step2(): Step {
        return StepBuilder("step2", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    println("step done")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }

    @Bean
    fun step3(): Step {
        return StepBuilder("step3", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    println("step3 done")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }
}
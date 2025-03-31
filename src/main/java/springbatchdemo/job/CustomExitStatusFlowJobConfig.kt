package springbatchdemo.job

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecutionListener
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
class CustomExitStatusFlowJobConfig (
        private val jobRepository: JobRepository,
        private val transactionManager: PlatformTransactionManager
) {

    // Step1의 성공여부에 따른 Step2,3 조건부 실행
    @Bean
    fun customExitStatusFlowJob(): Job {
        return JobBuilder("CustomExitStatusFlowJob", jobRepository)
                .incrementer(RunIdIncrementer())
                .start(customExitStatusFlowJob_step1())
                    .on("FAILED")
                    .to(customExitStatusFlowJob_step2())
                    .on("PASS")
                    .stop()
                    /* 아래 transition이 없어도, 특정 조건 만족을 못하면 (ex. PASS) 스프링배치가 자동으로 FAILED 처리
                .from(customExitStatusFlowJob_step2())
                .on("FAILED")
                .to(customExitStatusFlowJob_step1())
                     */
                .end()
                .build()
    }

    @Bean
    fun customExitStatusFlowJob_step1(): Step {
        return StepBuilder("CustomExitStatusFlowJob_step1", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    Thread.sleep(1000)
                    println("step1 done with failed status")
                    if (contribution != null) {

                        // Step1 ExitStatus를 Failed로 처리되도록
                        contribution.stepExecution.exitStatus = ExitStatus.FAILED
                    }

                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }

    @Bean
    fun customExitStatusFlowJob_step2(): Step {
        return StepBuilder("CustomExitStatusFlowJob_step2", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    println("step done")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }
}
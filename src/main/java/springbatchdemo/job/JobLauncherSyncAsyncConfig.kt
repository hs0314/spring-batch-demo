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
class JobLauncherSyncAsyncConfig (
        private val jobRepository: JobRepository,
        private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun jobLauncherSyncAsyncJob(jobLauncherSyncAsyncStep1: Step,
                        jobLauncherSyncAsyncStep2: Step): Job {
        return JobBuilder("jobLauncherSyncAsyncJob", jobRepository)
                .incrementer(RunIdIncrementer())
                .start(jobLauncherSyncAsyncStep1)
                .next(jobLauncherSyncAsyncStep2)
                .build()
    }

    @JobScope
    @Bean
    fun jobLauncherSyncAsyncStep1(): Step {
        return StepBuilder("jobLauncherSyncAsync1", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    Thread.sleep(1500)
                    println("jobLauncherSyncAsync1")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }

    @JobScope
    @Bean
    fun jobLauncherSyncAsyncStep2(): Step {
        return StepBuilder("jobLauncherSyncAsyncStep2", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    Thread.sleep(1500)
                    println("jobLauncherSyncAsyncStep2")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }
}
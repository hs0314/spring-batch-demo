package springbatchdemo.job

import org.springframework.batch.core.*
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.FlowExecution
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class JobExecutionDeciderConfig (
        private val jobRepository: JobRepository,
        private val transactionManager: PlatformTransactionManager
) {

    private var counter = 1

    // Step1의 성공여부에 따른 Step2,3 조건부 실행
    @Bean
    fun jobExecutionDeciderJob(): Job {
        return JobBuilder("JobExecutionDeciderJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(jobExecutionDeciderJob_step1())
            .next(decider())
            .from(decider()).on("ODD").to(jobExecutionDeciderJob_oddStep())
            .from(decider()).on("EVEN").to(jobExecutionDeciderJob_evenStep())
            .end()
            .build()
    }

    @Bean
    fun decider(): JobExecutionDecider {

        counter++

        // 자바의 익명클래스처럼 작성
        return object : JobExecutionDecider {

            override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
                return if (counter % 2 == 0) {
                    FlowExecutionStatus("EVEN")
                } else {
                    FlowExecutionStatus("ODD")
                }
            }
        }
    }

    @Bean
    fun jobExecutionDeciderJob_step1(): Step {
        return StepBuilder("JobExecutionDeciderJob_step1", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    println("JobExecutionDeciderJob_step1")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }

    @Bean
    fun jobExecutionDeciderJob_oddStep(): Step {
        return StepBuilder("JobExecutionDeciderJob_oddStep", jobRepository)
                .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                    println("jobExecutionDeciderJob_oddStep")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }

    @Bean
    fun jobExecutionDeciderJob_evenStep(): Step {
        return StepBuilder("JobExecutionDeciderJob_evenStep", jobRepository)
            .tasklet({ contribution: StepContribution?, chunkContext: ChunkContext? ->
                println("jobExecutionDeciderJob_evenStep")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}
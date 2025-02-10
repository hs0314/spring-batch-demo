package springbatchdemo.controller

import io.micrometer.common.util.StringUtils
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.job.SimpleJob
import org.springframework.batch.core.launch.JobOperator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import springbatchdemo.core.domain.JobInfo
import springbatchdemo.core.domain.Member
import java.util.*

@RestController
class JobController(
        // 스프링배치 운영 관련 객체
        private val jobRegistry: JobRegistry,
        private val jobOperator: JobOperator,
        private val jobExplorer: JobExplorer
) {

    @PostMapping("/batch/start")
    fun start(@RequestBody jobInfo: JobInfo): String {

        if (jobInfo.jobName.isNullOrEmpty()) {
            return "error"
        }

        for (jobName in jobRegistry.jobNames) {
            if (jobName.equals(jobInfo.jobName)) {
                println(jobName)

                // 이렇게 Job 참조 가능
                //val job: Job = jobRegistry.getJob(jobName) as SimpleJob
                val parameters = Properties().apply {
                    setProperty("id", jobInfo.id)
                    setProperty("timestamp", System.currentTimeMillis().toString())
                }

                jobOperator.start(jobName, parameters)
            }
        }

        return "batch is started!"
    }

    @PostMapping("/batch/stop")
    fun stop(@RequestBody jobInfo: JobInfo): String {

        if (jobInfo.jobName.isNullOrEmpty()) {
            return "error"
        }

        for (jobName in jobRegistry.jobNames) {
            if (jobName.equals(jobInfo.jobName)) {
                println(jobName)

                val jobExecutions: Set<JobExecution> = jobExplorer.findRunningJobExecutions(jobName)

                for (jobExecution in jobExecutions) {
                    jobOperator.stop(jobExecution.id);
                }
            }
        }

        return "batch is stopped!"
    }

    @PostMapping("/batch/restart")
    fun restart(@RequestBody jobInfo: JobInfo): String {

        if (jobInfo.jobName.isNullOrEmpty()) {
            return "error"
        }

        for (jobName in jobRegistry.jobNames) {
            if (jobName.equals(jobInfo.jobName)) {
                println(jobName)

                // JobExplorer를 통해서 마지막 실행 executionId를 가져오기
                val jobInstance: JobInstance = jobExplorer.getLastJobInstance(jobName)
                val jobExecution: JobExecution = jobExplorer.getLastJobExecution(jobInstance)

                jobOperator.restart(jobExecution.id)
            }
        }

        return "batch is restarted!"
    }
}
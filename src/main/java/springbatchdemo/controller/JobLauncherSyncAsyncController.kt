package springbatchdemo.controller

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.JobExecution
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import springbatchdemo.core.domain.Member
import java.util.*

@RestController
class JobLauncherSyncAsyncController(
        @Qualifier("jobLauncher")
        private val syncJobLauncher: JobLauncher,
        @Qualifier("asyncJobLauncher")
        private val asyncJobLauncher: JobLauncher, // 별도로 빈 설정
        private val jobLauncherSyncAsyncJob: Job
) {

    @PostMapping("/batch/sync")
    fun launchJobSync(@RequestBody member: Member): ResponseEntity<String> {
        val jobParameters = JobParametersBuilder()
                .addString("id", member.id)
                .addDate("date", Date())
                .toJobParameters()

        return try {
            val jobExecution: JobExecution = syncJobLauncher.run(jobLauncherSyncAsyncJob, jobParameters)
            ResponseEntity.ok("Sync Job Execution ID: ${jobExecution.id}, Status: ${jobExecution.status}")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Sync Job Failed: ${e.message}")
        }
    }

    @PostMapping("/batch/async")
    fun launchJobAsync(@RequestParam(defaultValue = "default") jobParam: String): ResponseEntity<String> {
        val jobParameters = JobParametersBuilder()
                .addString("jobParam", jobParam)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

        return try {
            asyncJobLauncher.run(jobLauncherSyncAsyncJob, jobParameters)
            ResponseEntity.ok("Async Job Started Successfully!")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Async Job Failed: ${e.message}")
        }
    }
}
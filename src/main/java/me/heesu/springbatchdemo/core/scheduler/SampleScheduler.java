package me.heesu.springbatchdemo.core.scheduler;

import me.heesu.springbatchdemo.job.HelloWorldJobConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Component
public class SampleScheduler {

    @Autowired
    private Job helloWorldJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Scheduled(cron = "0 */1  * * * *")
    public void helloWorldJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters params = new JobParameters(
                Collections.singletonMap("reqTime", new JobParameter(System.currentTimeMillis()))
        );

        jobLauncher.run(helloWorldJob, params);
    }
}

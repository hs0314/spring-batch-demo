package me.heesu.springbatchdemo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListener implements JobExecutionListener {

    private static String BEFORE_MSG = "{} job is running.";
    private static String AFTER_MSG = "{} job is done. (status : {})";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(BEFORE_MSG, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(AFTER_MSG, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());

        if(jobExecution.getStatus() == BatchStatus.FAILED){
            // todo: 추가 작업
            log.error("job is faild.");
        }
    }
}

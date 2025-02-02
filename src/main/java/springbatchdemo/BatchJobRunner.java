package springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Value("${spring.batch.job.names:}")
    private String jobName;

    public BatchJobRunner(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @Override
    public void run(String... args) throws Exception {
        if (jobName == null || jobName.isEmpty()) {
            System.out.println("No job name provided in 'spring.batch.job.names'. Exiting...");
            return;
        }

        try {
            // JobRegistry에서 Job 이름으로 Job 가져오기
            Job job = jobRegistry.getJob(jobName);
            System.out.println("Starting Job: " + jobName);

            jobLauncher.run(job, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 고유 파라미터 추가로 JobInstance 겹치지 않도록 함
                    .toJobParameters());

            System.out.println("Job Execution Completed!");
        } catch (Exception e) {
            System.err.println("Failed to execute job: " + jobName);
            e.printStackTrace();
        }
    }
}

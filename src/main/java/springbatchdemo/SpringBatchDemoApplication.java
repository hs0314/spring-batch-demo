package springbatchdemo;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchDemoApplication {

	@Autowired
	JobRegistry jobRegistry;

	@Bean
	public JobLauncher asyncJobLauncher(JobRepository jobRepository) {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기 TaskExecutor 설정
		return jobLauncher;
	}

	@Bean
	public BeanPostProcessor jobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);

		return jobRegistryBeanPostProcessor;
	}

	public static void main(String[] args) {

		SpringApplication.run(SpringBatchDemoApplication.class, args);
	}
}

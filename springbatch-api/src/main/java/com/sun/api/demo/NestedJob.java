package com.sun.api.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * job的嵌套
 * JobStepBuilder 可以将一个job转换为一个step
 *
 * @Date 2020/2/6 22:38
 */
@Configuration
public class NestedJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job childJobOne;

    @Autowired
    private Job childJobTwo;

    @Bean
    public Job parentJobs(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return jobBuilderFactory.get("parentJob")
                // 父job 包含2个子job
                .start(childJob1(jobRepository, transactionManager))
                .next(childJob2(jobRepository, transactionManager))
                .build();
    }


    private Step childJob1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobStepBuilder(new StepBuilder("childJob1"))
                .job(childJobOne)
                .launcher(jobLauncher)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .build();
    }


    private Step childJob2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobStepBuilder(new StepBuilder("childJob2"))
                .job(childJobTwo)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .build();
    }
}

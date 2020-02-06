package com.sun.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 子job1 1个step
 *
 * @Date 2020/2/6 22:33
 */
@Configuration
public class ChildJob1 {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    @Bean
    public Job childJobOne() {
        return jobBuilderFactory.get("childJobOne")
                .start(childJob1Step1())
                .build();
    }

    @Bean
    public Step childJob1Step1() {
        return stepBuilderFactory.get("childJob1Step1").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.err.println("childJob1Step1");
                return RepeatStatus.FINISHED;
            }
        }).build();
    }


}

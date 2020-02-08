package com.sun.api.demo;

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
 * 子job2 2个step
 *
 * @Date 2020/2/6 22:41
 */
@Configuration
public class ChildJob2 {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    @Bean
    public Job childJobTwo() {
        return jobBuilderFactory.get("childJobTwo")
                .start(childJob2Step1())
                .next(childJob2Step2())
                .build();
    }

    @Bean
    public Step childJob2Step1() {
        return stepBuilderFactory.get("childJob2Step1").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.err.println("childJob2Step1");
                return RepeatStatus.FINISHED;
            }
        }).build();
    }

    @Bean
    public Step childJob2Step2() {
        return stepBuilderFactory.get("childJob2Step2").tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.err.println("childJob2Step2");
                return RepeatStatus.FINISHED;
            }
        }).build();
    }
}

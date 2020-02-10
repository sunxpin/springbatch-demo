package com.sun.api.error;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Date 2020/2/10 18:59
 */
@Configuration
public class ErrorDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job errorDemoJob() {
        return jobBuilderFactory.get("errorDemoJob")
                .start(errorDemoStep1())
                .next(errorDemoStep2())
                .build();
    }

    @Bean
    public Step errorDemoStep1() {
        return stepBuilderFactory.get("errorDemoStep1")
                .tasklet(error1())
                .build();
    }

    @Bean
    public Step errorDemoStep2() {
        return stepBuilderFactory.get("errorDemoStep2")
                .tasklet(error1())
                .build();
    }

    @Bean
    public Tasklet error1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                if (executionContext.containsKey("sun")) {
                    System.out.println("执行成功。。。");
                    return RepeatStatus.FINISHED;
                } else {
                    chunkContext.getStepContext().getStepExecution().getExecutionContext().put("sun", true);
                    System.out.println("执行失败");
                    throw new RuntimeException("出异常了");
                }
            }
        };
    }
}

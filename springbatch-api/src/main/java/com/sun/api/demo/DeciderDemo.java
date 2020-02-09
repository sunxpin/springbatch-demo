package com.sun.api.demo;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义决策器
 *
 * @Date 2020/2/6 22:02
 */
@Configuration
public class DeciderDemo {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    @Bean
    public JobExecutionDecider myDecider() {
        // 创建自定义决策器对象
        return new JobExecutionDecider() {

            private int count;

            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                count++;
                if (count % 2 == 0)
                    return new FlowExecutionStatus("even");
                else
                    return new FlowExecutionStatus("odd");
            }
        };
    }

    /**
     * deciderDemoStep1()执行后进入决策器判断：
     * 若为奇数则执行 deciderDemoStep3()
     * 若为偶数则执行 deciderDemoStep2()
     * 以上2步执行后，
     * 从 deciderDemoStep3() ，无论结果如何（*） ，回到决策器重新执行
     *
     * @return
     */
    @Bean
    public Job deciderDemoJob() {
        return jobBuilderFactory.get("deciderDemoJob")
                .start(deciderDemoStep1())
                .next(myDecider())
                .from(myDecider()).on("odd").to(deciderDemoStep3())
                .from(myDecider()).on("even").to(deciderDemoStep2())
                .from(deciderDemoStep3()).on("*").to(myDecider())
                .end()
                .build();
    }


    @Bean
    public Step deciderDemoStep1() {
        return stepBuilderFactory.get("deciderDemoStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.err.println("deciderDemoStep1");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step deciderDemoStep2() {
        return stepBuilderFactory.get("deciderDemoStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.err.println("deciderDemoStep2");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step deciderDemoStep3() {
        return stepBuilderFactory.get("deciderDemoStep3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.err.println("deciderDemoStep3");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }
}

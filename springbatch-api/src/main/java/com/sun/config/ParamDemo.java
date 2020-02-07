package com.sun.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * job参数
 * {@link org.springframework.batch.core.JobParameter}
 *
 * @Date 2020/2/7 22:31
 */
@Configuration
public class ParamDemo implements StepExecutionListener {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    private Map<String, JobParameter> parameters;

    @Bean
    public Job paramDemoJobs1() {
        return jobBuilderFactory.get("paramDemoJobs1")
                // 带参数的Job
                .start(paramDemoStep())
                .build();
    }


    /**
     * Job执行的是Step，Job使用的数据肯定是在Step中使用，所以我只需给Step传递参数
     * <p>
     * 如何给Step传递参数呢？
     * 使用监听，使用Step级别的监听器来传递参数，
     * 因此让本类实现{@link org.springframework.batch.core.StepExecutionListener}接口
     *
     * @return
     */
    private Step paramDemoStep() {
        return stepBuilderFactory.get("paramDemoStep")
                // 直接使用本类监听，也可以重新写个类来监听
                .listener(this)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(parameters.get("info"));
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        parameters = stepExecution.getJobParameters().getParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}

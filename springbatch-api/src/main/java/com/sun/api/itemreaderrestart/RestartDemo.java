package com.sun.api.itemreaderrestart;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2020/2/8 16:53
 */
@Configuration
public class RestartDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemReader<Customer> restartReader;

    @Autowired
    private ItemWriter<Customer> restartWriter;

    @Bean
    public Job restartDemoJob() {
        return jobBuilderFactory.get("restartDemoJob")
                .start(restartDemoStep())
                .build();
    }

    @Bean
    public Step restartDemoStep() {
        return stepBuilderFactory.get("restartDemoStep")
                .<Customer, Customer>chunk(4)
                .reader(restartReader)
                .writer(restartWriter)
                .build();
    }
}

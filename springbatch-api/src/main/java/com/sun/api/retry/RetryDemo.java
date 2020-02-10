package com.sun.api.retry;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2020/2/10 19:17
 */
@Configuration
public class RetryDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemProcessor<String, String> retryDemoProcessor;

    @Bean
    public Job retryDemoJob() {
        return jobBuilderFactory.get("retryDemoJob")
                .start(retryDemoStep())
                .build();
    }

    @Bean
    public Step retryDemoStep() {
        return stepBuilderFactory.get("retryDemoStep")
                .<String, String>chunk(2)
                .reader(retryReader())
                .processor(retryDemoProcessor)
                .writer(list -> list.stream().forEach(System.out::println))

                .faultTolerant() //容错
                .retry(CustomerException.class)
                .retryLimit(5)

                .build();
    }

    @Bean
    public ListItemReader<String> retryReader() {
        List<String> item = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            item.add(String.valueOf(i));
        }
        return new ListItemReader<>(item);
    }

}

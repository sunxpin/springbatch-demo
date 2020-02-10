package com.sun.api.skip;

import com.sun.api.retry.CustomerException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
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
 * 异常跳过监听器
 * {@link SkipListener }
 *
 * @Date 2020/2/10 19:47
 */
@Configuration
public class SkipDemo {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemProcessor<String, String> skipDemoProcessor;

    @Autowired
    private SkipListener<String, String> mySkipListener;


    @Bean
    public Job skipListenerDemoJob() {
        return jobBuilderFactory.get("skipListenerDemoJob")
                .start(skipListenerDemoStep())
                .build();
    }

    @Bean
    public Step skipListenerDemoStep() {
        return stepBuilderFactory.get("skipListenerDemoStep")
                .<String, String>chunk(2)
                .reader(skipReader())
                .processor(skipDemoProcessor)
                .writer(list -> list.stream().forEach(System.out::println))

                .faultTolerant() //容错
                .skip(CustomerException.class)
                .skipLimit(5)
                .listener(mySkipListener)

                .build();
    }

    @Bean
    public ListItemReader<String> skipReader() {
        List<String> item = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            item.add(String.valueOf(i));
        }
        return new ListItemReader<>(item);
    }
}

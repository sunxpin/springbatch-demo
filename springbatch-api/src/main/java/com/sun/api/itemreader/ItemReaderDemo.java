package com.sun.api.itemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义读
 * {@link MyItemReader}
 * <p>
 * 写
 * {@link ListItemWriter}
 * <p>
 * 自定义监听器
 * {@link ChunkListener}
 *
 * @Date 2020/2/8 12:51
 */
@Configuration
public class ItemReaderDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemReaderDemoJob() {
        return jobBuilderFactory.get("itemReaderDemoJob")
                .start(itemReaderDemoStep())
                .build();
    }

    @Bean
    public Step itemReaderDemoStep() {
        return stepBuilderFactory.get("itemReaderDemoStep")
                .<String, String>chunk(2)
                .reader(itemReaderDemoReader())
                .writer(list -> list.stream().forEach(System.out::println))
                .listener(new ChunkListener())
                .build();
    }

    private MyItemReader itemReaderDemoReader() {
        List<String> list = Arrays.asList("Fox", "Pig", "Horse", "Cat");
        return new MyItemReader(list);
    }
}

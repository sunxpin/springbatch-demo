package com.sun.api.demo;

import com.sun.api.demo.listener.MyCHunkListener;
import com.sun.api.demo.listener.MyJobListener;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 监听器
 * {@link JobExecutionListener#beforeJob(JobExecution)}
 * {@link JobExecutionListener#afterJob(JobExecution)} (JobExecution)}
 * <p>
 * {@link StepExecutionListener#beforeStep(StepExecution)}
 * {@link StepExecutionListener#afterStep(StepExecution)}
 * <p>
 * {@link ChunkListener#beforeChunk(ChunkContext)}}
 * {@link ChunkListener#afterChunk(ChunkContext)}}
 * <p>
 * {@link ItemReadListener#beforeRead()}
 * {@link ItemReadListener#afterRead(Object)} ()}
 * <p>
 * {@link ItemProcessListener#beforeProcess(Object)}
 * {@link ItemProcessListener#afterProcess(Object, Object)}
 * <p>
 * {@link ItemWriteListener#beforeWrite(List)}
 * {@link ItemWriteListener#afterWrite(List)}
 *
 * @Date 2020/2/7 20:45
 */
@Configuration
public class ListenerDemo {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job listenerDemoJobss() {
        return jobBuilderFactory.get("listenerDemoJobss")
                .start(listenerDemoStep())
                // Job监听器
                .listener(new MyJobListener())
                .build();
    }

    /**
     * 注：chunk是跟着step的
     *
     * @return
     */
    @Bean
    public Step listenerDemoStep() {
        return stepBuilderFactory.get("listenerDemoStep")
                .<String, String>chunk(2)
                // 允许出错
                .faultTolerant()

                // Chunk监听器
                .listener(new MyCHunkListener())
                .reader(read())
                .writer(write())
                .build();
    }

    private ItemWriter<String> write() {
        return list -> list.stream().forEach(System.out::println);
    }

    private ItemReader<String> read() {
        return new ListItemReader<>(Arrays.asList("Java", "Spring Batch", "Spring WebFlux"));
    }
}

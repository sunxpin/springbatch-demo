package com.sun.api.itemreaderfile;

import com.sun.api.itemreader.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 从普通文件中读取数据
 * 读
 * {@link FlatFileItemReader}
 * {@link DelimitedLineTokenizer}
 * {@link DefaultLineMapper}
 * <p>
 * 自定义写
 * {@link FlatFileWriter}
 *
 * @Date 2020/2/8 14:21
 */
@Configuration
public class ItemReaderFlatFileDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemWriter<Customer> flatFileWriter;

    @Bean
    public Job itemReaderFlatFileDemoJob() {
        return jobBuilderFactory.get("itemReaderFlatFileDemoJob")
                .start(itemReaderFlatFileStep())
                .build();
    }

    @Bean
    public Step itemReaderFlatFileStep() {
        return stepBuilderFactory.get("itemReaderFlatFileStep")
                .<Customer, Customer>chunk(5)
                .reader(flatFileReader())
                .writer(flatFileWriter)
                .listener(new ChunkListener())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> flatFileReader() {
        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setResource(new ClassPathResource("customer.txt"));
        reader.setLinesToSkip(1);

        // 解词器
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "firstName", "lastName", "birthday");

        // 把读到的每一行数据转换为对象
        DefaultLineMapper<Customer> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldSet -> Customer.builder().id(fieldSet.readLong("id"))
                .firstName(fieldSet.readString("firstName"))
                .lastName(fieldSet.readString("lastName"))
                .birthday(fieldSet.readString("birthday"))
                .build());
        mapper.afterPropertiesSet();

        reader.setLineMapper(mapper);
        return reader;
    }
}

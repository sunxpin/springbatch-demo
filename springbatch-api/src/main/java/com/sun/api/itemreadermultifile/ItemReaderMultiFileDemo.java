package com.sun.api.itemreadermultifile;

import com.sun.api.itemreader.ChunkListener;
import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 读取多个文件,需要依赖单个文件的读取
 *
 * @Date 2020/2/8 15:45
 */
@Configuration
public class ItemReaderMultiFileDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemWriter<Customer> multiFileWriterDemo;

    @Value("classpath:/file*.txt")
    private Resource[] resources;

    @Bean
    public Job itemReaderMultiFileDemoJob() {
        return jobBuilderFactory.get("itemReaderMultiFileDemoJob")
                .start(itemReaderMultiFileStep())
                .build();
    }

    @Bean
    public Step itemReaderMultiFileStep() {
        return stepBuilderFactory.get("itemReaderMultiFileStep")
                .<Customer, Customer>chunk(10)
                .reader(multiFileReader())
                .writer(multiFileWriterDemo)
                .listener(new ChunkListener())
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<Customer> multiFileReader() {
        MultiResourceItemReader reader = new MultiResourceItemReader();
        reader.setDelegate(flatFileReader());
        reader.setResources(resources);
        return reader;
    }


    @Bean
    @StepScope
    public FlatFileItemReader<Customer> flatFileReader() {
        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setResource(new ClassPathResource("customer.txt"));
//        reader.setLinesToSkip(1);

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

package com.sun.api.itemwriterdb;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * 将数据从普通文件写到到数据库中
 * 读
 * {@link FlatFileItemReader}
 * <p>
 * 写
 * {@link JdbcBatchItemWriter}
 *
 * @Date 2020/2/9 15:46
 */
@Configuration
public class ItemWriterDBDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;


    @Bean
    public Job itemWriterDBDemoJob() {
        return jobBuilderFactory.get("itemWriterDBDemoJob")
                .start(itemWriterDBStep())
                .build();

    }

    @Bean
    public Step itemWriterDBStep() {
        return stepBuilderFactory.get("itemWriterDBStep")
                .<Customer, Customer>chunk(5)
                .reader(itemReaderDBReader())
                .writer(itemWriterDBWriter())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> itemWriterDBWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter();
        writer.setDataSource(dataSource);

        String sql = "insert into customer (id , firstName, lastName, birthday) values (:id, :firstName, :lastName, :birthday)";
        writer.setSql(sql);

        // 占位符赋值
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        return writer;
    }

    @Bean
    public FlatFileItemReader<Customer> itemReaderDBReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("customer.txt"));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "firstName", "lastName", "birthday");

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);

        lineMapper.setFieldSetMapper(new FieldSetMapper<Customer>() {
            @Override
            public Customer mapFieldSet(FieldSet fieldSet) {
                return Customer.builder().id(fieldSet.readLong("id"))
                        .firstName(fieldSet.readString("firstName"))
                        .lastName(fieldSet.readString("lastName"))
                        .birthday(fieldSet.readString("birthday"))
                        .build();
            }
        });

        reader.setLineMapper(lineMapper);
        return reader;
    }
}

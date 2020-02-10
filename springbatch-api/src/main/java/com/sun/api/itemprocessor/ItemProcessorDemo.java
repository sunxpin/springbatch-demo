package com.sun.api.itemprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 多个processor
 * {@link CompositeItemProcessor}
 *
 * @Date 2020/2/10 8:57
 */
@Configuration
public class ItemProcessorDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private ItemProcessor<Customer, Customer> demoProcessor;

    @Autowired
    private ItemProcessor<Customer, Customer> demoProcessor2;

    @Bean
    public Job itemProcessorDemoJobs() throws Exception {
        return jobBuilderFactory.get("itemProcessorDemoJobs")
                .start(itemProcessorStep())
                .build();
    }

    @Bean
    public Step itemProcessorStep() throws Exception {
        return stepBuilderFactory.get("itemProcessorStep")
                .<Customer, Customer>chunk(2)
                .reader(dbReader())

                /*.processor(demoProcessor)*/

                .processor(chainProcessor())

                .writer(flatFileWriter())
                .build();
    }

    @Bean
    public CompositeItemProcessor<Customer, Customer> chainProcessor() {
        CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(demoProcessor, demoProcessor2));
        return compositeItemProcessor;
    }


    @Bean
    public ItemWriter<Customer> flatFileWriter() throws Exception {
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter();
        writer.setResource(new FileSystemResource("F:\\customer3.txt"));

        writer.setLineAggregator(customer -> {
            ObjectMapper objectMapper = new ObjectMapper();
            String result = null;
            try {
                result = objectMapper.writeValueAsString(customer);
            } catch (JsonProcessingException e) {
            }
            return result;
        });
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public JdbcPagingItemReader<Customer> dbReader() throws Exception {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader();
        reader.setDataSource(dataSource);
        reader.setFetchSize(2);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        Map<String, Order> sort = new HashMap<>(1);
        sort.put("id", Order.DESCENDING);

        queryProvider.setSelectClause("id,firstName,lastName,birthday");
        queryProvider.setFromClause("from customer");
        queryProvider.setSortKeys(sort);

        reader.setQueryProvider(queryProvider);
        reader.setRowMapper((resultSet, i) ->
                Customer.builder().id(resultSet.getInt(1))
                        .firstName(resultSet.getString(2))
                        .lastName(resultSet.getString(3))
                        .birthday(resultSet.getString(4))
                        .build());
        reader.afterPropertiesSet();
        return reader;
    }
}

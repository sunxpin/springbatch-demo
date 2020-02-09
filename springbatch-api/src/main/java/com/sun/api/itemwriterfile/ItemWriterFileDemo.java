package com.sun.api.itemwriterfile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 将数据库数据写入到普通文件中
 * <p>
 * 读
 * {@link JdbcPagingItemReader}
 * <p>
 * 写
 * {@link FlatFileItemWriter}
 *
 * @Date 2020/2/9 17:04
 */
@Configuration
public class ItemWriterFileDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;


    @Bean
    public Job itemWriterFileDemoJobs2() throws Exception {
        return jobBuilderFactory.get("itemWriterFileDemos2")
                .start(itemWriterFileStep())
                .build();
    }

    @Bean
    public Step itemWriterFileStep() throws Exception {
        return stepBuilderFactory.get("itemWriterFileStep")
                .<Customer, Customer>chunk(2)
                .reader(itemWriterFileReader())
                .writer(itemWriterFileWriter())
                .build();
    }

    @Bean
    public FlatFileItemWriter<Customer> itemWriterFileWriter() throws Exception {
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter();
        writer.setResource(new FileSystemResource("F:\\customer.txt"));

        writer.setLineAggregator(new LineAggregator<Customer>() {
            @Override
            public String aggregate(Customer customer) {
                ObjectMapper objectMapper = new ObjectMapper();
                String result = null;
                try {
                    result = objectMapper.writeValueAsString(customer);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public JdbcPagingItemReader<Customer> itemWriterFileReader() {
        JdbcPagingItemReader<Customer> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(2);

        jdbcPagingItemReader.setRowMapper((resultSet, i) ->
                Customer.builder().id(resultSet.getInt(1))
                        .firstName(resultSet.getString(2))
                        .lastName(resultSet.getString(3))
                        .birthday(resultSet.getString(4))
                        .build());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id,firstName,lastName,birthday");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sort = new HashMap<>(1);
        sort.put("id", Order.DESCENDING);  // 降序
        queryProvider.setSortKeys(sort);

        jdbcPagingItemReader.setQueryProvider(queryProvider);
        return jdbcPagingItemReader;
    }
}

package com.sun.api.itemwriterxml;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 将数据库数据写入到xml文件中
 * <p>
 * 读
 * {@link JdbcPagingItemReader}
 * <p>
 * 写
 * {@link StaxEventItemWriter}
 * {@link XStreamMarshaller}
 *
 * @Date 2020/2/9 22:21
 */
@Configuration
public class ItemWriterFileXMLDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Bean
    public Job itemWriterXMLJob() {
        return jobBuilderFactory.get("itemWriterXMLJob")
                .start(itemWriterXMLStep())
                .build();
    }

    @Bean
    public Step itemWriterXMLStep() {
        return stepBuilderFactory.get("itemWriterXMLStep")
                .<Customer, Customer>chunk(2)
                .reader(itemWriterXMLReader())
                .writer(itemWriterXMLWriter())
                .build();

    }

    @Bean
    public StaxEventItemWriter<Customer> itemWriterXMLWriter() {
        StaxEventItemWriter<Customer> xmlWriter = new StaxEventItemWriter<>();
        xmlWriter.setResource(new FileSystemResource("F:\\customer.xml"));
        xmlWriter.setRootTagName("customers");

        XStreamMarshaller marshaller = new XStreamMarshaller();
        Map<String, Class> alias = new HashMap<>();
        alias.put("customer", Customer.class);
        marshaller.setAliases(alias);

        xmlWriter.setMarshaller(marshaller);
        return xmlWriter;
    }

    @Bean
    public JdbcPagingItemReader<Customer> itemWriterXMLReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(2);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id,firstName,lastName,birthday");
        queryProvider.setFromClause("from customer");
        Map<String, Order> sort = new HashMap<>(1);
        sort.put("id", Order.DESCENDING);
        queryProvider.setSortKeys(sort);

        reader.setRowMapper((resultSet, i) -> Customer.builder()
                .id(resultSet.getLong(1))
                .firstName(resultSet.getString(2))
                .lastName(resultSet.getString(3))
                .birthday(resultSet.getString(4))
                .build());
        reader.setQueryProvider(queryProvider);
        return reader;
    }
}

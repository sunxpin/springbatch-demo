package com.sun.api.itemwritermultifile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将数据库数据分别输出到customer2.txt和customer2.xml中
 * 读
 * {@link JdbcPagingItemReader}
 * <p>
 * 写
 * {@link CompositeItemWriter#setDelegates(List)} 不分类输出
 * {@link ClassifierCompositeItemWriter} 分类输出
 *
 * @Date 2020/2/9 22:16
 */
@Configuration
public class ItemWriterMultiFileDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Bean
    public Job itemWriterMultiFileDemoJobs() throws Exception {
        return jobBuilderFactory.get("itemWriterMultiFileDemoJobs")
                .start(itemWriterMultiFileStep())
                .build();
    }

    @Bean
    public Step itemWriterMultiFileStep() throws Exception {
        return stepBuilderFactory.get("itemWriterMultiFileStep")
                .<Customer, Customer>chunk(4)
                .reader(itemReaderMultiFileReader())

                /* .
                 .writer(itemWriterMultiFileWriter())
                 */

                .writer(classifierWriter())
                .stream(jsonWirter())
                .stream(xmlWriter())


                .build();
    }

    /**
     * 不分类输出
     *
     * @return
     * @throws Exception
     */
    @Bean
    public CompositeItemWriter<Customer> itemWriterMultiFileWriter() throws Exception {
        CompositeItemWriter<Customer> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(jsonWirter(), xmlWriter()));
        return compositeItemWriter;
    }

    /**
     * 分类输出
     *
     * @return
     */
    @Bean
    public ClassifierCompositeItemWriter<Customer> classifierWriter() {
        ClassifierCompositeItemWriter<Customer> classifierWriter = new ClassifierCompositeItemWriter<>();
        classifierWriter.setClassifier(new Classifier<Customer, ItemWriter<? super Customer>>() {
            @Override
            public ItemWriter<Customer> classify(Customer customer) {
                try {
                    // id为偶数写入到txt中，否则写入到xml中
                    return customer.getId() % 2 == 0 ? jsonWirter() : xmlWriter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return classifierWriter;
    }


    @Bean
    public JdbcPagingItemReader<Customer> itemReaderMultiFileReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(4);

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

    @Bean
    public StaxEventItemWriter<Customer> xmlWriter() {
        StaxEventItemWriter<Customer> xmlWriter = new StaxEventItemWriter<>();

        xmlWriter.setResource(new FileSystemResource("F:\\customer2.xml"));
        xmlWriter.setRootTagName("customers");

        XStreamMarshaller marshaller = new XStreamMarshaller();
        Map<String, Class> alias = new HashMap<>();
        alias.put("customer", Customer.class);
        marshaller.setAliases(alias);

        xmlWriter.setMarshaller(marshaller);
        return xmlWriter;
    }

    @Bean
    public FlatFileItemWriter<Customer> jsonWirter() throws Exception {
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter();
        writer.setResource(new FileSystemResource("F:\\customer2.txt"));

        writer.setLineAggregator(customer -> {
            ObjectMapper objectMapper = new ObjectMapper();
            String result = null;
            try {
                result = objectMapper.writeValueAsString(customer);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return result;
        });
        writer.afterPropertiesSet();
        return writer;
    }
}

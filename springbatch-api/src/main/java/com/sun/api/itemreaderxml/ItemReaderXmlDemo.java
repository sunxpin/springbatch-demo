package com.sun.api.itemreaderxml;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

/**
 * xml文件读取
 *
 * @Date 2020/2/8 15:13
 */
@Configuration
public class ItemReaderXmlDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemWriter<Customer> xmlWriterDemo;

    @Bean
    public Job itemReaderXMLDemoJob() {
        return jobBuilderFactory.get("itemReaderXMLDemoJob")
                .start(itemReaderXMLDemoStep())
                .build();
    }

    @Bean
    public Step itemReaderXMLDemoStep() {
        return stepBuilderFactory.get("itemReaderXMLDemoStep")
                .<Customer, Customer>chunk(2)
                .reader(xmlReader())
                .writer(xmlWriterDemo)
                .build();
    }

    @Bean
    public StaxEventItemReader<Customer> xmlReader() {
        StaxEventItemReader reader = new StaxEventItemReader();
        reader.setResource(new ClassPathResource("customer.xml"));

        // 指定需要处理的根标签
        reader.setFragmentRootElementName("customer");

        // 把xml装成对象
        XStreamMarshaller unmarshaller = new XStreamMarshaller();
        Map<String, Class> alias = new HashMap<>(1);
        alias.put("customer", Customer.class);
        unmarshaller.setAliases(alias);
        reader.setUnmarshaller(unmarshaller);
        return reader;
    }
}

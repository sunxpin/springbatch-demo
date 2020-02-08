package com.sun.api.itemreadermultifile;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Date 2020/2/8 15:50
 */
@Component
public class MultiFileWriterDemo implements ItemWriter<Customer> {
    @Override
    public void write(List<? extends Customer> list) throws Exception {
        list.stream().forEach(System.out::println);
    }
}

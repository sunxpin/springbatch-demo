package com.sun.api.itemreaderfile;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Date 2020/2/8 14:26
 */
@Component
public class FlatFileWriter implements ItemWriter<Customer> {
    @Override
    public void write(List<? extends Customer> list) {
        list.stream().forEach(System.out::println);
    }
}

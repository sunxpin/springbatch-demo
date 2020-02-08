package com.sun.api.itemreaderdb;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义 ItemWriter
 *
 * @Date 2020/2/8 14:03
 */
@Component
public class JdbcWriterDemo implements ItemWriter<User> {
    @Override
    public void write(List<? extends User> list) throws Exception {
        list.stream().forEach(System.out::println);
    }
}

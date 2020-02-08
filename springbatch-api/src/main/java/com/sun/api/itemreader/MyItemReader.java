package com.sun.api.itemreader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;

/**
 * 自定义ItemReader
 *
 * @Date 2020/2/8 13:03
 */
public class MyItemReader implements ItemReader<String> {
    private final Iterator<String> iterator;

    public MyItemReader(List<String> list) {
        this.iterator = list.iterator();
    }

    @Override
    public String read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        // 一次读取一个数据
        if (this.iterator.hasNext()) {
            return iterator.next();
        } else
            return null;
    }
}

package com.sun.api.itemreaderrestart;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Date 2020/2/8 16:58
 */
@Component
public class RestartWriter implements ItemStreamWriter<Customer> {

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
    }

    @Override
    public void write(List<? extends Customer> list) {
        list.stream().forEach(System.out::println);
    }
}

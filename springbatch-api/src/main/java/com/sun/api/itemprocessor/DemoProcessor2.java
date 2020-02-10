package com.sun.api.itemprocessor;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @Date 2020/2/10 9:50
 */
@Component
public class DemoProcessor2 implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        if (customer.getId() % 2 == 0) {
            return customer;
        } else
            return null;
    }
}

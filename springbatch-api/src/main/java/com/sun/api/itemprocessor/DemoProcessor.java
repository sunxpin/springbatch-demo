package com.sun.api.itemprocessor;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @Date 2020/2/10 9:46
 */
@Component
public class DemoProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        customer.setFirstName(customer.getFirstName().toUpperCase());
        return customer;
    }
}

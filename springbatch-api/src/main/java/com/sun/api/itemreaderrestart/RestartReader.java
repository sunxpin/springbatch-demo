package com.sun.api.itemreaderrestart;

import com.sun.api.itemreaderfile.Customer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * @Date 2020/2/8 16:58
 */
@Component
public class RestartReader implements ItemStreamReader<Customer> {

    private FlatFileItemReader<Customer> customerFlatFileItemReader = new FlatFileItemReader<>();
    private ExecutionContext executionContext;
    private Boolean restart = false;
    private Long curLine;

    public RestartReader() {
        customerFlatFileItemReader.setResource(new ClassPathResource("restart.txt"));
        customerFlatFileItemReader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "firstName", "lastName", "birthday");
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> Customer.builder()
                .id(fieldSet.readLong("id"))
                .firstName(fieldSet.readString("firstName"))
                .lastName(fieldSet.readString("lastName"))
                .birthday(fieldSet.readString("birthday"))
                .build());
        lineMapper.afterPropertiesSet();

        customerFlatFileItemReader.setLineMapper(lineMapper);

    }

    @Override
    public Customer read() throws Exception {
        this.curLine++;
        if (restart) {
            customerFlatFileItemReader.setLinesToSkip(this.curLine.intValue() - 1);
            restart = false;
            System.out.println("start read from line :" + this.curLine);
        }

        customerFlatFileItemReader.open(executionContext);
        Customer customer = customerFlatFileItemReader.read();
        if (customer != null && customer.getFirstName().equals("error")) {
            throw new RuntimeException("读取异常");
        }
        return customer;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        if (executionContext.containsKey("curLine")) {
            this.curLine = executionContext.getLong("curLine");
            this.restart = true;
        } else {
            this.curLine = 0L;
            executionContext.put("curLine", this.curLine);
            System.out.println("starting read from line :" + this.curLine + 1L);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext.put("curLine", this.curLine);
        System.out.println("当前读到第 " + this.curLine + " 行");
    }

    @Override
    public void close() throws ItemStreamException {
    }
}

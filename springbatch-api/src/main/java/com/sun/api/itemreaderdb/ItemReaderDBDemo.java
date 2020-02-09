package com.sun.api.itemreaderdb;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * 从数据库读取数据,并自定义ItemWriter将数据输出
 * 读
 * {@link JdbcPagingItemReader}
 * <p>
 * 写
 * {@link JdbcWriterDemo}
 *
 * @Date 2020/2/8 13:33
 */
@Configuration
public class ItemReaderDBDemo {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private ItemWriter<User> jdbcWriterDemo;

    @Bean
    public Job itemReaderDBDemoJob() {
        return jobBuilderFactory.get("itemReaderDBDemoJob")
                .start(itemReaderDBDemoStep())
                .build();
    }

    @Bean
    public Step itemReaderDBDemoStep() {
        return stepBuilderFactory.get("itemReaderDBDemoStep")
                .<User, User>chunk(2)

                // 从数据库读取数据
                .reader(jdbcReaderDemo())

                // 自定义写数据
                .writer(jdbcWriterDemo)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<User> jdbcReaderDemo() {
        JdbcPagingItemReader<User> jdbcPagingItemReader = new JdbcPagingItemReader<>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(2);

        // 把数据库记录转换为对象
        jdbcPagingItemReader.setRowMapper(new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                return User.builder().id(resultSet.getInt(1))
                        .username(resultSet.getString(2))
                        .password(resultSet.getString(3))
                        .age(resultSet.getInt(4))
                        .build();
            }
        });

        // 指定Sql语句
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id,username,password,age");// 查询的字段
        queryProvider.setFromClause("from user"); // 从哪张表查

        Map<String, Order> sort = new HashMap<>(1);
        sort.put("age", Order.DESCENDING);
        queryProvider.setSortKeys(sort); // 根据age降序

        jdbcPagingItemReader.setQueryProvider(queryProvider);
        return jdbcPagingItemReader;
    }
}

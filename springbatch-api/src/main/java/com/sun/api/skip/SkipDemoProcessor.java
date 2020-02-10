package com.sun.api.skip;

import com.sun.api.retry.CustomerException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @Date 2020/2/10 19:49
 */
@Component
public class SkipDemoProcessor implements ItemProcessor<String, String> {
    private int attemptCount;

    @Override
    public String process(String item) throws Exception {
        System.out.println("当前读到第" + item + "个");
        if (item.equals("26")) {
            attemptCount++;
            if (attemptCount >= 4) {
                System.out.println("第" + attemptCount + "次succss");
                return String.valueOf(Integer.valueOf(item) * -1);
            } else {
                System.out.println("第" + attemptCount + "次执行失败");
                throw new CustomerException("执行失败");
            }
        } else {
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }
}

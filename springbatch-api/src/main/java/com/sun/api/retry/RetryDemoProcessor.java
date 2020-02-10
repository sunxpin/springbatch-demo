package com.sun.api.retry;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @Date 2020/2/10 19:22
 */
@Component
public class RetryDemoProcessor implements ItemProcessor<String, String> {

    private int attemptCount;

    @Override
    public String process(String string) {
        System.out.println("当前读到第" + string + "个");
        if (string.equals("26")) {
            attemptCount++;
            if (attemptCount >= 3) {
                System.out.println("第" + attemptCount + "次succss");
                return String.valueOf(Integer.valueOf(string) * -1);
            } else {
                System.out.println("第" + attemptCount + "次执行失败");
                throw new CustomerException("执行失败");
            }
        } else {
            return String.valueOf(Integer.valueOf(string) * -1);
        }
    }
}

package com.sun.api.skip;

import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

/**
 * @Date 2020/2/10 19:59
 */
@Component
public class MySkipListener implements SkipListener<String, String> {

    @Override
    public void onSkipInRead(Throwable throwable) {

    }

    @Override
    public void onSkipInWrite(String s, Throwable throwable) {

    }

    @Override
    public void onSkipInProcess(String s, Throwable throwable) {
        System.out.println("第" + s + "个出现异常，出现得异常为：" + throwable.getMessage());
    }
}

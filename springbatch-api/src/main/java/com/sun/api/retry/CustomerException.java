package com.sun.api.retry;

/**
 * @Date 2020/2/10 19:33
 */
public class CustomerException extends RuntimeException {

    public CustomerException(String s) {
        super(s);
    }

    public CustomerException() {
        super();
    }
}

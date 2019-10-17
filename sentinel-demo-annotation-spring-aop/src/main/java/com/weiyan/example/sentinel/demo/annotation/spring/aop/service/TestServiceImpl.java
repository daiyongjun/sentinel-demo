package com.weiyan.example.sentinel.demo.annotation.spring.aop.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * 基础业务类
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/10/16 14:42
 */
@Service
public class TestServiceImpl implements TestService {
    @Override
    @SentinelResource(value = "abc", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public void test() {
        System.out.println("Test");
    }

    @Override
    @SentinelResource(value = "abc", fallback = "helloFallback")
    public String hello(long s) {
        if (s < 0) {
            throw new IllegalArgumentException("invalid arg");
        }
        return String.format("Hello at %d", s);
    }

    @Override
    @SentinelResource(value = "abc", defaultFallback = "defaultFallback",
            exceptionsToIgnore = {IllegalStateException.class})
    public String helloAnother(String name) {
        if (name == null || "bad".equals(name)) {
            throw new IllegalArgumentException("oops");
        }
        if ("foo".equals(name)) {
            throw new IllegalStateException("oops");
        }
        return "Hello, " + name;
    }

    @SuppressWarnings("unused")
    public String helloFallback(long s, Throwable ex) {
        // Do some log here.
        ex.printStackTrace();
        return "Oops, error occurred at " + s;
    }

    @SuppressWarnings("unused")
    public String defaultFallback() {
        System.out.println("Go to default fallback");
        return "default_fallback";
    }
}

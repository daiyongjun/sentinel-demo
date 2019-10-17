package com.weiyan.example.sentinel.demo.annotation.spring.aop.service;

/**
 * 基础业务类
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/10/16 14:40
 */
public interface TestService {
    void test();

    String hello(long s);

    String helloAnother(String name);
}

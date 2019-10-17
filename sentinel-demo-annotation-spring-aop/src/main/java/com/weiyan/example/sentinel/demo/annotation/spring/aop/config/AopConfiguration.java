package com.weiyan.example.sentinel.demo.annotation.spring.aop.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置sentinel的引入类
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/10/16 14:38
 */
@Configuration
public class AopConfiguration {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
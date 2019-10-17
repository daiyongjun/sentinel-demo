package com.weiyan.example.sentinel.demo.annotation.spring.aop.controller;

import com.weiyan.example.sentinel.demo.annotation.spring.aop.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务启动映射
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/10/16 14:48
 */
@RestController
public class DemoController {
    private final TestService service;

    @Autowired
    public DemoController(TestService service) {
        this.service = service;
    }

    @GetMapping("/foo")
    public String apiFoo(@RequestParam(required = false) Long t) {
        if (t == null) {
            t = System.currentTimeMillis();
        }
        service.test();
        return service.hello(t);
    }

    @GetMapping("/baz/{name}")
    public String apiBaz(@PathVariable("name") String name) {
        return service.helloAnother(name);
    }
}

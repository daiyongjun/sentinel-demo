package com.weiyan.example.sentinel.demo.annotation.spring.aop.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * 使用blockHandlerClass，异常处理类
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/10/16 14:39
 */
public final class ExceptionUtil {
    @SuppressWarnings("unused")
    public static void handleException(BlockException ex) {
        // Handler method that handles BlockException when blocked.
        // The method parameter list should match original method, with the last additional
        // parameter with type BlockException. The return type should be same as the original method.
        // The block handler method should be located in the same class with original method by default.
        // If you want to use method in other classes, you can set the blockHandlerClass
        // with corresponding Class (Note the method in other classes must be static).
        System.out.println("Oops: " + ex.getClass().getCanonicalName());
    }
}

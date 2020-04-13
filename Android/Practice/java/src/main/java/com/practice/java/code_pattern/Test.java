package com.practice.java.code_pattern;

import com.practice.java.code_pattern.proxy.DynamicProxy;
import com.practice.java.thread.producer_consumer.ProducerConsumer;

/**
 * @author SuK
 * @time 2020/4/13 10:57
 * @des
 */
public class Test {
    public static void main(String[] args) {
        DynamicProxy dynamicProxy = new DynamicProxy();
        dynamicProxy.test();
    }
}

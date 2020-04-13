package com.practice.java.thread;

import com.practice.java.thread.producer_consumer.ProducerConsumer;

/**
 * @author SuK
 * @time 2020/4/13 10:57
 * @des
 */
public class Test {
    public static void main(String[] args) {
        ProducerConsumer producerConsumer = new ProducerConsumer();
        producerConsumer.run();
    }
}

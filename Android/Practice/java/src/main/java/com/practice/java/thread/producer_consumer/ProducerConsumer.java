package com.practice.java.thread.producer_consumer;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @author SuK
 * @time 2020/4/13 10:59
 * @des
 */
public class ProducerConsumer {

    private final Object obj = new Object();
    private final int MAX = 10;
    private int mData = 0;

    private Runnable mConsumer = new Runnable() {
        @Override
        public void run() {
            synchronized (obj) {

                while (mData <= 0) {
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mData--;
                obj.notifyAll();
                System.out.println("消防了一个，还有" + mData);
            }
        }
    };

    private Runnable mProducer = new Runnable() {
        @Override
        public void run() {
            synchronized (obj) {

                while (mData >= MAX) {
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mData++;
                obj.notifyAll();
                System.out.println("生产了一个，还有" + mData);

            }
        }
    };


    public void run() {
        HashSet<Thread> set = new HashSet<>();

        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mProducer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));
        set.add(new Thread(mConsumer));

        for (Thread thread : set) {
            thread.start();
        }
    }
}

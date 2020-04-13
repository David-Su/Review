package com.practice.java.code_pattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author SuK
 * @time 2020/4/13 14:55
 * @des
 */
public class DynamicProxy {


    interface Student{
        void doHomework();
    }

    public void test(){
        final Student xiaoming = new Student(){

            @Override
            public void doHomework() {
                System.out.println("写作业");
            }
        };
        Student studentProxy = (Student) Proxy.newProxyInstance(Student.class.getClassLoader(), new Class[]{Student.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("借小红的作业");
                method.invoke(xiaoming);
                System.out.println("抄完了");
                return null;
            }
        });
        studentProxy.doHomework();
    }

}

package com.example.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SuK
 * @time 2020/6/17 14:42
 * @des
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface RuntimeAnno {

    int value();

}

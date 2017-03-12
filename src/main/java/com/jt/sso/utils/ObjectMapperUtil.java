package com.jt.sso.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by 王兆琦  on 2017/2/18 09.36.
 */
public class ObjectMapperUtil {


    //创建全局唯一对象
    // 为了线程安全， 可以用 volatile 关键字，一般用的少， 使用静态内部类巧妙达到并发安全
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //为了并发安全，使用静态内部类，来进行唯一创建单例对象
    //JSL规范定义，类的构造必须是原子性的，非并发的，因此不需要加同步块。
    private static class SingleInstanceHolder {
        private static final ObjectMapper objectMapper = new ObjectMapper();
    }

    //私有化构造函数
    private void ObjectMapper() {}

    //提供get方法
    public static ObjectMapper getInstance() {

        return SingleInstanceHolder.objectMapper;
    }
}

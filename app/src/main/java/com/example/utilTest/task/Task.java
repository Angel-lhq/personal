package com.example.utilTest.task;

public interface Task<T, R> {

    R call(Builder builder, T t);

}
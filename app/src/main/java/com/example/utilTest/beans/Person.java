package com.example.utilTest.beans;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * 博客地址：http://blog.csdn.net/gdutxiaoxu
 * @author xujun
 * @time 2017/3/29 22:19.
 */
public class Person {

    public String country;
    public String city;
    private String name;
    private String province;
    private Integer height;
    private Integer age;


    public Person() {
        System.out.println("调用Person的无参构造方法");
    }

    private Person(String country, String city, String name) {
        this.country = country;
        this.city = city;
        this.name = name;
    }

    public Person(String country, Integer age) {
        this.country = country;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private String getMobile(String number) {
        String mobile = "010-110" + "-" + number;
        return mobile;
    }


    private void setCountry(String country) {
        this.country=country;

    }

    public void getGenericHelper(HashMap<String, Integer> hashMap) {
    }

    public Class getGenericType() {
        try {
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            Method method = getClass().getDeclaredMethod("getGenericHelper",HashMap.class);
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            if (null == genericParameterTypes || genericParameterTypes.length < 1) {
                return null;
            }

            ParameterizedType parameterizedType=(ParameterizedType)genericParameterTypes[0];
            Type rawType = parameterizedType.getRawType();
            System.out.println("----> rawType=" + rawType);
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments==genericParameterTypes || actualTypeArguments.length<1) {
                return null;
            }

            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type type = actualTypeArguments[i];
                System.out.println("----> type=" + type);
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String toString() {
        return "Person{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", height=" + height +
                '}';
    }
}

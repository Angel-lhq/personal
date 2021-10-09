package com.example.utilTest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;

import com.example.utilTest.R;
import com.example.utilTest.beans.Person;
import com.example.utilTest.utils.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflexActivity extends AppCompatActivity {

    private static final String TAG = "ReflexActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflex);
        initView();
    }

    private void initView() {
        TestHelper.testConstructor();
        TestHelper.testFiled();
        TestHelper.testMethod();
        try {
            Class<?> aClass = Class.forName(TestHelper.CLASS_NAME);
            Constructor<?> constructor = aClass.getDeclaredConstructor((Class<?>) null);
            Object instance = constructor.newInstance();
            Person person = (Person) instance;
            Method method = aClass.getDeclaredMethod("getMobile",String.class);
            method.setAccessible(true);
            String mobile = (String) method.invoke(person,"6245");
            Log.i(TAG,"mobile is " + mobile);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static class TestHelper {

        public static final String TAG = "xujun";
        public static final String CLASS_NAME = "com.example.utilTest.beans.Person";
        public static final String CHINA = "China";

        public static void testConstructor() {
            ReflectHelper.printConstructor(CLASS_NAME);
            Constructor constructor = ReflectHelper.getConstructor(CLASS_NAME, String.class, Integer.class);
            try {
                Object meinv = constructor.newInstance(CHINA, 12);
                Person person = (Person) meinv;
                Log.i(TAG, "testConstructor: =" + person.toString());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        public static void testFiled() {
            ReflectHelper.printField(CLASS_NAME);
            Person person = new Person(CHINA, 12);
            Field field = ReflectHelper.getFiled(CLASS_NAME, "age");
            try {
                Integer integer = (Integer) field.get(person);
                PrintUtils.print("integer=" + integer);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        public static void testMethod() {
            ReflectHelper.printMethods(CLASS_NAME);
            Person person = new Person();
            Method method = ReflectHelper.getMethod(CLASS_NAME,
                    "setCountry", String.class);
            try {
                // 执行方法，结果保存在 person 中
                Object o = method.invoke(person, CHINA);
                // 拿到我们传递进取的参数 country 的值 China
                String country = person.country;
                PrintUtils.print(country);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }


    public static class ReflectHelper {

        private static final String TAG = "ReflectHelper";

        public static Method getMethod(String className, String methodName, Class<?>... clzs) {
            try {
                Class<?> aClass = Class.forName(className);
                Method declaredMethod = aClass.getDeclaredMethod(methodName, clzs);
                declaredMethod.setAccessible(true);
                return declaredMethod;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void printMethods(String className) {
            try {
                Class<?> aClass = Class.forName(className);
                Method[] declaredMethods = aClass.getDeclaredMethods();
                PrintUtils.print(declaredMethods);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        public static Field getFiled(String className, String filedName) {
            Object o = null;
            try {
                Class<?> aClass = Class.forName(className);

                Field declaredField = aClass.getDeclaredField(filedName);
                //   if not public,you should call this
                declaredField.setAccessible(true);
                return declaredField;


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;

        }

        public static void printField(String className) {
            try {
                Class<?> aClass = Class.forName(className);
                Field[] fields = aClass.getFields();
                PrintUtils.print(fields);
                Field[] declaredFields = aClass.getDeclaredFields();
                PrintUtils.print(declaredFields);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //获取所有构造方法
        public static void printConstructor(String className) {
            try {
                Class<?> aClass = Class.forName(className);
                Constructor<?>[] constructors = aClass.getConstructors();
                PrintUtils.print(constructors);
                Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
                PrintUtils.print(declaredConstructors);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        public static Constructor getConstructor(String className, Class<?>... clzs) {
            try {
                Class<?> aClass = Class.forName(className);
                Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(clzs);
                PrintUtils.print(declaredConstructor);
                //   if Constructor is not public,you should call this
                declaredConstructor.setAccessible(true);
                return declaredConstructor;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;

        }

    }

    private static class PrintUtils {

        public static void print(Field[] fields) {
            for (int i = 0; i < fields.length; i++) {
                Log.i(TAG, fields[i].getName());
            }
        }

        private static void print(Constructor<?> declaredConstructor) {
            Log.i(TAG, declaredConstructor.getName());
        }

        public static void print(Constructor<?>[] constructors) {
            for (int i = 0; i < constructors.length; i++) {
                Log.i(TAG, constructors[i].getName());
            }
        }

        public static void print(Method[] declaredMethods) {
            for (int i = 0; i < declaredMethods.length; i++) {
                Log.i(TAG, declaredMethods[i].getName());
            }
        }

        public static void print(String string) {
            Log.i(TAG, string);
        }
    }

}
package com.example.utilTest.kotlin

import java.util.*

class KtUtil (val x: Int,val y:Int) {

    val isEquals get() = x == y

    val isEquals1:Boolean
        get() {
            return x == y
        }

    fun isEqual():Boolean{
        return x == y
    }

    val isEquals2:Boolean = x == y

    val test = fun() {

    }

    fun main() {
        val str = "leavesC"
        println(str[1])
        for (c in str) {
            println(c)
        }
        val str1 = str + " hello"
        val intValue = 100
        //可以直接包含变量
        println("intValue value is $intValue") //intValue value is 100
        //也可以包含表达式
        println("(intValue + 100) value is ${intValue + 100}")   //(intValue + 100) value is 200
        val price = "${'$'}100.99"
        println(price)  //$100.99

    }

    fun arrays(){
        //包含给定元素的字符串数组
        val array1 = arrayOf("leavesC", "叶", "https://github.com/leavesC")

        array1[0] = "leavesC"
        println(array1[1])
        println(array1.size)

        //初始元素均为 null ，大小为 10 的字符数组
        val array2 = arrayOfNulls<String>(10)

        //创建从 “a” 到 “z” 的字符串数组
        val array3 = Array(26) { i -> ('a' + i).toString() }
        //指定数组大小，包含的元素将是对应基本数据类型的默认值(int 的默认值是 0)
        val intArray = IntArray(5)
        //指定数组大小以及用于初始化每个元素的 lambda
        val doubleArray = DoubleArray(5) { Random().nextDouble() }
        //接收变长参数的值来创建存储这些值的数组
        val charArray = charArrayOf('H', 'e', 'l', 'l', 'o')

    }


    sealed class ViewV {

        fun click() {

        }


        class ButtonV: ViewV(){

        }
        class TextViewV : ViewV() {

        }
    }
}
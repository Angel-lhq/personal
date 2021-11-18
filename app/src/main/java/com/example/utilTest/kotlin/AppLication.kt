package com.example.utilTest.kotlin

import android.app.Application
import com.example.utilTest.beans.Person
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module

open class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //开启koin
        startKoin {
            //设置log级别
            AndroidLogger(Level.DEBUG)
            //注入context，方法module中get()获取Context
            androidContext(this@AppApplication)
            //设置module
            modules(appModule)
        }
    }

    //所有需要通过koin依赖注入的类必须在这里创建对象，以MainViewModel为例
    val appModule =  module {
        single { Person() }
//        single { Person("CN",14) }
//        single { named("") }
        single { KtUtil(0,1) }
    }
}
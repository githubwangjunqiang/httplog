package com.example.logcache

import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.launch

/**
 * @data 2021/4/9
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
class App : Application() {

    private var my = MyCo()
    private var start = false
    private var startTime = 0L
    override fun onCreate() {
        super.onCreate()


        Looper.getMainLooper().setMessageLogging {
            my.launch {
                if (!start) {
                    start = true
                    startTime = System.currentTimeMillis()
                } else {
                    val durration = System.currentTimeMillis() - startTime
                    Log.i("Looper", "setMessageLogging: $durration ms  $it")
                    start = false
                }
            }

        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)


    }
}
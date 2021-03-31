package com.xq.app.cachelog

import android.annotation.SuppressLint
import android.content.Context
import com.xq.app.cachelog.`interface`.ILogCacheManager
import com.xq.app.cachelog.dao.LogDBHelper
import com.xq.app.cachelog.entiy.LogHttpCacheData
import kotlinx.coroutines.*

@SuppressLint("StaticFieldLeak")
object LogCacheManager : ILogCacheManager {
    /**
     * 全局上下文
     */
    var context: Context? = null

    /**
     * 用户自定义标识
     */
    var userId: String? = null

    /**
     * 缓存所用协程
     */
    private var launchJob = GlobalScope

    /**
     * 初始化时间  也作为此次进程所有日志保存的文件夹
     */
    var initTime = 0L
    override fun initContext(context: Context, userId: String) {
        this.context = context.applicationContext
        this.userId = userId
        initTime = System.currentTimeMillis()
    }

    override fun saveLoadLog(logDataCacheData: LogHttpCacheData?) {
        logDataCacheData?.let {
            it.logId = System.currentTimeMillis()
            launchJob.launch(Dispatchers.IO) {
                try {
                    LogDBHelper.singleton.addLogData(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }


}
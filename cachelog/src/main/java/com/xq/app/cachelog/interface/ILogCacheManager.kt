package com.xq.app.cachelog.`interface`

import android.content.Context
import com.xq.app.cachelog.entiy.LogHttpCacheData

interface ILogCacheManager {
    /**
     * 初始化
     * @param context app 的上下文 全局上下文
     */
    fun initContext(context: Context, userId: String)

    /**
     * 存入日志  请求接口的日志
     * @param logDataCacheData 日志的对象
     */
    fun saveLoadLog(logDataCacheData: LogHttpCacheData?)

    /**
     * 获取日志集合
     */
    suspend fun getLogs(startIndex: Long, count: Long): List<LogHttpCacheData>?

    /**
     * 获取总行数
     */
    suspend fun getLogCounts(): Long

    /**
     * 显示日志
     */
    fun showLogActivity(context: Context)


}
package com.xq.app.cachelog.`interface`

import android.content.Context
import com.xq.app.cachelog.entiy.LogHttpCacheData

interface ILogCacheManager {
    /**
     * 初始化
     * @param context app 的上下文 全局上下文
     */
    fun initContext(context: Context)

    /**
     * 存入日志  请求接口的日志
     * @param logDataCacheData 日志的对象
     */
    fun saveLoadLog(logDataCacheData: LogHttpCacheData?)



}
package com.xq.app.cachelog.entiy

import android.content.ContentValues
import androidx.annotation.Keep

@Keep
open class LogHttpCacheData {
    companion object {
        /**
         * 表名称
         */
        const val TABLE_NAME = "LogCacheAppHttp"

        /**
         * 主键
         */
        const val logId_key = "logId"

        /**
         * 请求时间 键
         */
        const val startTime_key = "startTime_key"

        /**
         * 用户标识 键
         */
        const val userId_key = "userId_key"

        /**
         * 此次接口链接 键
         */
        const val url_key = "url_key"

        /**
         * 请求头 键
         */
        const val sendHead_key = "sendHead_key"

        /**
         * 请求参数 键
         */
        const val sendParameter_key = "sendParameter_key"

        /**
         * 接口返回http 状态码 键
         */
        const val returnHttpCode_key = "returnHttpCode_key"

        /**
         * 接口耗时 键
         */
        const val durration_key = "durration_key"

        /**
         * 接口返回头 键
         */
        const val returnHeader_key = "returnHeader_key"

        /**
         * 接口返回信息 键
         */
        const val returnString_key = "returnString_key"

        /**
         * 自定义消息键 键
         */
        const val customMessage_key = "customMessage_key"


        /**
         * 创建表 语句
         */
        const val TABLE_CREATE = "create table $TABLE_NAME (" +
                "$logId_key INT PRIMARY KEY," +
                "$startTime_key INT," +
                "$userId_key TEXT," +
                "$url_key TEXT," +
                "$sendHead_key TEXT," +
                "$sendParameter_key TEXT," +
                "$returnHttpCode_key TEXT," +
                "$durration_key TEXT," +
                "$returnHeader_key TEXT," +
                "$returnString_key TEXT," +
                "$customMessage_key TEXT" +
                ")"


    }

    /**
     * 日志id标识付
     */
    var logId: Long = 0

    /**
     * 用户 标识
     */
    var userId: String? = null

    /**
     * 请求时间 毫秒值
     */
    var startTime: Long? = null


    /**
     * 此次接口链接
     */
    var url: String? = null

    /**
     * 请求头
     */
    var sendHead: String? = null

    /**
     * 请求参数
     */
    var sendParameter: String? = null


    /**
     * 接口耗时  单位毫秒
     */
    var durration: String? = null
    /**
     * 接口返回http 状态码
     */
    var returnHttpCode: String? = null

    /**
     * 接口返回头
     */
    var returnHeader: String? = null

    /**
     * 接口返回信息
     */
    var returnString: String? = null

    /**
     * 自定义消息
     */
    var customMessage: String? = null

    /**
     * 生成插入数据 value
     */
    fun createContentValues(): ContentValues {
        return ContentValues().apply {
            put(logId_key, logId)
            put(userId_key, userId)
            put(startTime_key, startTime)
            put(url_key, url)
            put(sendHead_key, sendHead)
            put(sendParameter_key, sendParameter)
            put(returnHttpCode_key, returnHttpCode)
            put(durration_key, durration)
            put(returnHeader_key, returnHeader)
            put(returnString_key, returnString)
            put(customMessage_key, customMessage)
        }
    }

}

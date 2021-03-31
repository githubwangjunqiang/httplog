package com.xq.app.cachelog.utils

import android.database.Cursor
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.xq.app.cachelog.entiy.LogHttpCacheData

/**
 * @data 2021/3/31
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
/**
 * 获取 loadHttpLogData 类型
 */
fun Cursor.loadHttpLogData(): LogHttpCacheData {
    var data = LogHttpCacheData(loadLong(LogHttpCacheData.logId_key))
    data.userId = loadString(LogHttpCacheData.userId_key)
    data.durration = loadString(LogHttpCacheData.durration_key)
    data.startTime = loadLong(LogHttpCacheData.startTime_key)
    data.returnHeader = loadString(LogHttpCacheData.returnHeader_key)
    data.returnHttpCode = loadString(LogHttpCacheData.returnHttpCode_key)
    data.sendHead = loadString(LogHttpCacheData.sendHead_key)
    data.sendParameter = loadString(LogHttpCacheData.sendParameter_key)
    data.returnString = loadString(LogHttpCacheData.returnString_key)
    data.customMessage = loadString(LogHttpCacheData.customMessage_key)
    return data
}

/**
 * 获取 long 类型
 */
fun Cursor.loadLong(columnName: String): Long {
    val columnIndex = getColumnIndex(columnName)
    if (columnIndex != -1) {
        return getLongOrNull(columnIndex) ?: 0
    }
    return 0
}

/**
 * 获取 String 类型
 */
fun Cursor.loadString(columnName: String): String? {
    val columnIndex = getColumnIndex(columnName)
    if (columnIndex != -1) {
        return getStringOrNull(columnIndex)
    }
    return null
}
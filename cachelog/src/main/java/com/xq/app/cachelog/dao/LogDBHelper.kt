package com.xq.app.cachelog.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.xq.app.cachelog.LogCacheManager
import com.xq.app.cachelog.entiy.LogHttpCacheData
import com.xq.app.cachelog.utils.loadHttpLogData
import com.xq.app.cachelog.utils.loadLong
import com.xq.app.cachelog.utils.loadString

class LogDBHelper : SQLiteOpenHelper {
    companion object {
        // 如果更改数据库架构，则必须增加数据库版本。
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "LogDBHelper.db"

        /**
         * 一次要查询的行数
         */
        const val limitCount = 100

        /**
         * 单例
         */
        val singleton: LogDBHelper by lazy {
            LogDBHelper(LogCacheManager.context!!)
        }
    }

    constructor(
        context: Context,
        factory: SQLiteDatabase.CursorFactory? = null
    ) : super(context, DATABASE_NAME, factory, DATABASE_VERSION)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(LogHttpCacheData.TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    /**
     * 添加一个
     */
    suspend fun addLogData(data: LogHttpCacheData): Long {
        val contentValues = data.createContentValues()
        return writableDatabase.replace(LogHttpCacheData.TABLE_NAME, null, contentValues)
    }

    /**
     * 获取分页数据
     * @param startIndex 从第几行开始
     * @param count  返回几行数据
     */
    suspend fun loadLogDataList(startIndex: Long, count: Long): MutableList<LogHttpCacheData> {
        var query: Cursor? = null
        var listData = mutableListOf<LogHttpCacheData>()
        try {
            query = writableDatabase.query(
                LogHttpCacheData.TABLE_NAME, null, null,
                null, null, null, "${LogHttpCacheData.logId_key} desc",
//                null, null, null, null,
                "$startIndex,$count"
            )
            query?.let {
                if (it.moveToFirst()) {
                    listData.add(it.loadHttpLogData())
                    while (it.moveToNext()) {
                        listData.add(it.loadHttpLogData())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            query?.close()
        }
        return listData
    }


    /**
     * 获取所有行数
     */
    suspend fun loadDataCount(): Long {
        var count = 0L
        var rawQuery: Cursor? = null
        try {
            rawQuery =
                writableDatabase.rawQuery(
                    "select count(*)  from ${LogHttpCacheData.TABLE_NAME}",
                    null
                )
            if (rawQuery.moveToFirst()) {
                count = rawQuery.getLong(0)
            }
            Log.d("12345", "loadDataCount: $count")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rawQuery?.close()
            return count
        }
    }

    /**
     * 组合删除指定日期的数据
     */
    suspend fun deleteCount(count: Long) {
        var rawQuery: Cursor? = null
        writableDatabase.beginTransaction()
        try {
            val queryFistID = queryForIndex(count)
            queryFistID?.run {
                deleteLessThan(this)
            }
            writableDatabase.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rawQuery?.close()
            writableDatabase.endTransaction()
        }


    }

    /**
     * 查询第一条id
     */
    suspend fun queryForIndex(index: Long): String? {
        var rawQuery: Cursor? = null
        var logIds: String? = null
        try {
            rawQuery =
                writableDatabase.query(
                    LogHttpCacheData.TABLE_NAME,
                    arrayOf(LogHttpCacheData.logId_key),
                    null, null, null, null, null, "$index,1"
                )

            if (rawQuery?.moveToFirst() == true) {
                val logId: Long = rawQuery?.loadLong(LogHttpCacheData.logId_key)
                logIds = if (logId == 0L) null else logId.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            rawQuery?.close()
            return logIds
        }
    }

    /**
     * 删除id 小于指定行的
     * @param logId  行 id
     */
    suspend fun deleteLessThan(logId: String): Boolean {
        var success = false
        try {

            val count = writableDatabase.delete(
                LogHttpCacheData.TABLE_NAME,
                "${LogHttpCacheData.logId_key}<?",
                arrayOf(logId)
            )
            Log.d("12345", "delete: $count")
            if (count == 1) {
                success = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return success
    }

}
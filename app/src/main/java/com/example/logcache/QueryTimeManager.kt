package com.example.logcache

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import android.util.Log
import com.xq.app.cachelog.utils.format
import com.xq.app.cachelog.utils.show

/**
 * @data 2021/4/22
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
object QueryTimeManager {


    /**
     * 查询时间跨度内的事件流  返回变更后的起始时间
     * @param context 全局上下文
     * @param startTime 起始时间
     * @param endTime  结束时间
     * @return 变更后的起始查询时间 如果内部计算不能变更 会返回 入参startTime 的值
     */
    fun queryTime(
        context: Context,
        startTime: Long,
        endTime: Long,
        packageName: String
    ): QueryTimeData {
        val queryTimeData = QueryTimeData(0)

        if (!havePermissionForTest(context)) {
            return queryTimeData
        }
        val usermananger: UserManager =
            context.getSystemService(Context.USER_SERVICE) as UserManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!usermananger.isUserUnlocked) {
                return queryTimeData
            }
        }
        val systemService: UsageStatsManager = context
            .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val queryEvents =
//            systemService.queryEvents(1619077116000, 1619077147000)
            systemService.queryEvents(startTime, endTime)

        if (!queryEvents.hasNextEvent()) {
            "没有查询到任何数据".show()
            return queryTimeData
        }
        val eventOut = UsageEvents.Event()

        //记录时长
        var time = 0L
        //开始时间
        var startTime = 1619077116000
        //每次更新的type
        var pauseType = 0
        //是否有开始
        var showResumed = false

        val ployTime = PloyTime()
        while (queryEvents.getNextEvent(eventOut)) {
            val packageName = eventOut.packageName
            val timeStamp = eventOut.timeStamp
            val eventType = eventOut.eventType
            if ("com.maxgames.stickwarlegacy" != eventOut.packageName) {
                Log.d("12345", "包名不对:${eventOut.packageName} ")
                continue
            }
            when (eventType) {
                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if (pauseType == eventType) {
                        continue
                    }
                    time += timeStamp - startTime
                    pauseType = eventType
                    ployTime.addEventType(timeStamp,eventType)
                }
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    startTime = timeStamp
                    pauseType = eventType
                    ployTime.addEventType(timeStamp,eventType)
                }
            }
        }





        Log.d("12345", "alltime:${time} ")

        return queryTimeData
    }

    fun havePermissionForTest(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val packageManager = context.packageManager
                val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
                val appOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
                mode == AppOpsManager.MODE_ALLOWED
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }

    fun goToSettingIntent(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
                e1.message.show()
            }
        }
    }

}
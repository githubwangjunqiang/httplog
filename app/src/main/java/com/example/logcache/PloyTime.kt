package com.example.logcache

import android.app.usage.UsageEvents

/**
 * @data 2021/4/22
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
class PloyTime {

    private var eventTypeResumed = false
    private var eventTypePause = false
    private var lastType = 0

    //上次时间戳
    private var lastTimestamp = 0L

    fun addEventType(timestamp: Long, eventType: Int) {
        if (eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
            eventTypePause = true
        }
        if (eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
            eventTypeResumed = true
        }
        lastType = eventType
        lastTimestamp = timestamp
    }

    fun loadLastTime(startTime: Long, endTime: Long, time: Long): Long {
        var duration = 0L
        // 无开始  无结束
        if (!eventTypeResumed && !eventTypePause) {
            duration = 0
        }
        // 无开始  有结束
        if (!eventTypeResumed && eventTypePause) {
            duration = time
        }
        // 有开始  无结束
        if (eventTypeResumed && !eventTypePause) {
            val timeLeft = endTime - lastTimestamp
            duration = time + timeLeft
        }
        // 有开始  有结束
        if (eventTypeResumed && eventTypePause) {
            //不是结束 结尾事件流 要按照又开始无结束算
            if (lastType != UsageEvents.Event.ACTIVITY_PAUSED) {
                val timeLeft = endTime - lastTimestamp
                duration = time + timeLeft
            }
        }
        return duration
    }

}
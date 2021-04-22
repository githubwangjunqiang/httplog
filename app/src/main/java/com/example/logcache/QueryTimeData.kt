package com.example.logcache

/**
 * @data 2021/4/22
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
data class QueryTimeData(
    /**
     * 毫秒值 时长
     */
    var duration: Long = 0,
    /**
     * 计算起始值 如果 不是-1 那么说明起始值发生变化
     */
    var startTime: Long = -1
)

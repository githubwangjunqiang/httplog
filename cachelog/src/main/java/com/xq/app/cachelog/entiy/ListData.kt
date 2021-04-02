package com.xq.app.cachelog.entiy

/**
 * @data 2021/4/1
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
class ListData {
    companion object {
        /**
         * 普通 类型
         */
        const val ITEM_TYPE = 0

        /**
         * 加载类型
         */
        const val LOAD_TYPE = 1

        /**
         * 未加载状态
         */
        const val UN_LOAD_STATS = 0

        /**
         * 加载中
         */
        const val LOADING_STATS = 1
    }

    var data: LogHttpCacheData? = null

    /**
     * 类型
     */
    var itemType: Int = ITEM_TYPE

    /**
     * 加载状态
     */
    var loadStats: Int = UN_LOAD_STATS


}
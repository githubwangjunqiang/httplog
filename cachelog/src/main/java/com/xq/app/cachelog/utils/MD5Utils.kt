package com.xq.app.cachelog.utils

import java.security.MessageDigest

object MD5Utils {


    /**
     * md5
     */
    fun loadMd5(content: String): String {

        try {
            val instance = MessageDigest.getInstance("MD5")
            instance.reset()
            val digest = instance.digest(content.toByteArray())
            val stringBuilder = StringBuilder()
            for (byte in digest) {
                val i: Int = byte.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    // 容错处理，长度小于2的，自动补0
                    stringBuilder.append("0")
                }
                stringBuilder.append(hexString)
            }
            return stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return content

    }
}
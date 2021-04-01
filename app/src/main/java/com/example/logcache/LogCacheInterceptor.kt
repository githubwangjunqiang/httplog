package com.example.logcache

import com.xq.app.cachelog.LogCacheManager
import com.xq.app.cachelog.entiy.LogHttpCacheData
import okhttp3.*
import okio.Buffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * @data 2021/3/31
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
class LogCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val logData = LogHttpCacheData(System.currentTimeMillis())
        logData.userId = LogCacheManager.userId
        val request = chain.request()

        val body: RequestBody? = request.body
        val url: HttpUrl = request.url
        logData.url = "${request.method}->$url"
        val sbHeader = StringBuilder()
        val headers = request.headers
        headers.forEach {
            sbHeader.append(it.first).append("=").append(it.second).append("\n")
        }
        logData.sendHead = sbHeader.toString()
        sbHeader.delete(0, sbHeader.length)
        body?.run {
            if (this is FormBody) {
                val buffer = Buffer()
                this.writeTo(buffer)
                val charset = this.contentType()?.charset(Charset.defaultCharset())
                    ?: Charset.defaultCharset()
                var readString = buffer.readString(charset)
                logData.sendParameter = readString
            }
        }

        val startNs = System.nanoTime()
        LogCacheManager.saveLoadLog(logData)
        var proceed = chain.proceed(request)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        logData.durration = tookMs.toString()

        logData.returnHttpCode = "httpCode:${proceed.code};httpMessage:${proceed.message};"

        //是否可以打印
        proceed.headers?.forEach {
            sbHeader.append(it.first).append("=").append(it.second).append("\n")
        }
        logData.returnHeader = sbHeader.toString()
        sbHeader.delete(0, sbHeader.length)
        val responseBody: ResponseBody? = proceed.body
        responseBody?.let {
            val subtype = it.contentType()?.subtype
            if (subtype.equals("json")) {
                val string = it.string()
                logData.returnString = string
                val create = ResponseBody.create(it.contentType(), string)
                proceed = proceed.newBuilder()
                    .body(create)
                    .build()
            }

        }
        LogCacheManager.saveLoadLog(logData)
        return proceed
    }


    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !"identity".equals(contentEncoding, ignoreCase = true)
    }
}
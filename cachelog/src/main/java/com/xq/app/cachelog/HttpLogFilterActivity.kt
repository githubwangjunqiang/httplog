package com.xq.app.cachelog

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xq.app.cachelog.adapter.LogAdapter
import com.xq.app.cachelog.entiy.ListData
import com.xq.app.cachelog.entiy.LogHttpCacheData
import kotlinx.coroutines.*

@Keep
open class HttpLogFilterActivity : AppCompatActivity() {



    private var mMainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log_filter)

        initView()

        setListener()


    }

    /**
     * 初始化 view
     */
    private fun initView() {


    }

    /**
     * 设置监听器
     */
    private fun setListener() {

    }



    override fun onDestroy() {
        mMainScope?.cancel()
        super.onDestroy()
    }
}
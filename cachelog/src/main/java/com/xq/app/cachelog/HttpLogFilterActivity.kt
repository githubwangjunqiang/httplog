package com.xq.app.cachelog

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xq.app.cachelog.adapter.LogAdapter
import com.xq.app.cachelog.entiy.ListData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

@Keep
open class HttpLogFilterActivity : AppCompatActivity() {


    private var recyclerView: RecyclerView? = null
    private var mLogAdapter: LogAdapter? = null
    private var mMainScope = MainScope()

    companion object {
        fun startActivity(httpLogActivity: HttpLogActivity, filter: List<ListData>) {
            listData.clear()
            listData.addAll(filter)
            httpLogActivity.startActivity(
                Intent(
                    httpLogActivity,
                    HttpLogFilterActivity::class.java
                )
            )
        }

        val listData = arrayListOf<ListData>()
    }


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
        recyclerView = findViewById(R.id.xp_log_http_activity_recyclerview)
        mLogAdapter = LogAdapter(this, mMainScope) {
        }
        mLogAdapter?.list?.addAll(listData)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = mLogAdapter
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    }

    /**
     * 设置监听器
     */
    private fun setListener() {

    }


    override fun onDestroy() {
        mMainScope?.cancel()
        listData.clear()
        super.onDestroy()
    }
}
package com.xq.app.cachelog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

open class HttpLogActivity : AppCompatActivity() {


    private var logCounts: Long = 0
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log)
        swipeRefreshLayout = findViewById(R.id.xp_log_http_activity_swiperefreshlayout)
        recyclerView = findViewById(R.id.xp_log_http_activity_recyclerview)


        swipeRefreshLayout?.setOnRefreshListener {
            GlobalScope.launch {
                logCounts = LogCacheManager.getLogCounts()
                if (logCounts <= 0) {
                    Toast.makeText(this@HttpLogActivity, "没有数据", Toast.LENGTH_SHORT).show()
                } else {
                    logCounts-100
//                    LogCacheManager.getLogs()
                }
            }
        }


    }

    override fun onDestroy() {
        GlobalScope.cancel()
        super.onDestroy()
    }
}
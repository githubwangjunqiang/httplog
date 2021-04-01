package com.xq.app.cachelog

import android.os.Bundle
import android.util.Log
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
open class HttpLogActivity : AppCompatActivity() {


    private var count: Long = 100
    private var startIndex: Long = 0
    private var logCounts: Long = 0
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mLogAdapter: LogAdapter? = null

    private var jobRefresh: Job? = null
    private var jobLoading: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log)
        swipeRefreshLayout = findViewById(R.id.xp_log_http_activity_swiperefreshlayout)
        recyclerView = findViewById(R.id.xp_log_http_activity_recyclerview)
        mLogAdapter = LogAdapter(this) {
            loadMoreData()
        }
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = mLogAdapter
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        swipeRefreshLayout?.setOnRefreshListener {
            loadData()
        }

        swipeRefreshLayout?.post {
            swipeRefreshLayout?.isRefreshing = true
            loadData()
        }

    }

    private fun loadMoreData() {
        jobLoading?.cancel()
        jobLoading = GlobalScope.launch {
            try {
                if (startIndex <= 0) {
                    addData(null)
                    return@launch
                }
                var index = 0L
                index = if (startIndex - count <= 0) {
                    0
                }else{
                    startIndex - count
                }
                var counts = if (index == 0L) startIndex else count
                startIndex = index
                val logs = LogCacheManager.getLogs(startIndex, counts)
                addData(logs)
            } catch (e: CancellationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                addData(null)
            }
        }
    }

    private fun loadData() {
        jobRefresh?.cancel()
        jobRefresh = GlobalScope.launch {
            try {
                logCounts = LogCacheManager.getLogCounts()
                if (logCounts <= 0) {
                    Toast.makeText(this@HttpLogActivity, "没有数据", Toast.LENGTH_SHORT).show()
                    setData(null)
                } else {
                    if (logCounts <= count) {
                        startIndex = 0
                    } else {
                        startIndex = logCounts - count
                    }
                    var counts = if (startIndex == 0L) logCounts else count
                    val logs = LogCacheManager.getLogs(startIndex, counts)
                    setData(logs)
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                setData(null)
            }
        }
    }

    /**
     * 设置数据
     */
    private fun setData(logs: List<LogHttpCacheData>?) {
        Log.d("12345", "setData:${logs?.size} ")
        runOnUiThread {
            swipeRefreshLayout?.isRefreshing = false
            mLogAdapter?.let {
                it.list.clear()
                val mutableListOf = mutableListOf<ListData>()

                logs?.forEach {
                    mutableListOf.add(ListData().apply { this.data = it })
                }
                if (mutableListOf.isNotEmpty()) {
                    mutableListOf.add(ListData().apply { itemType = ListData.LOAD_TYPE })
                }
                it.list.addAll(mutableListOf)
                it.notifyDataSetChanged()
                runOnUiThread {
                    it.loadingStats = false
                }
            }
        }


    }

    /**
     * 添加数据
     */
    private fun addData(logs: List<LogHttpCacheData>?) {
        Log.d("12345", "addData: ${logs?.size}")
        runOnUiThread {
            mLogAdapter?.let {
                val mutableListOf = mutableListOf<ListData>()
                logs?.forEach {
                    mutableListOf.add(ListData().apply { this.data = it })
                }
                if (mutableListOf.isNotEmpty()) {
                    mutableListOf.add(ListData().apply { itemType = ListData.LOAD_TYPE })
                }
                it.list.removeLastOrNull()
                it.list.addAll(mutableListOf)
                it.notifyDataSetChanged()
                runOnUiThread {
                    it.loadingStats = mutableListOf.isEmpty()
                }

            }
        }


    }

    override fun onDestroy() {
        jobLoading?.cancel()
        jobRefresh?.cancel()
        super.onDestroy()
    }
}
package com.xq.app.cachelog

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
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
open class HttpLogActivity : AppCompatActivity() {


    private var count: Long = 100
    private var startIndex: Long = 0
    private var logCounts: Long = 0
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var tvFilter: View? = null
    private var btnNext: View? = null
    private var btnPrevious: View? = null
    private var etKeyword: EditText? = null
    private var tvTitle: TextView? = null
    private var ivLogo: ImageView? = null
    private var tvCount: TextView? = null
    private var mLogAdapter: LogAdapter? = null

    private var jobRefresh: Job? = null
    private var jobLoading: Job? = null
    private var mMainScope = MainScope()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log)

        initView()

        setListener()


        swipeRefreshLayout?.post {
            swipeRefreshLayout?.isRefreshing = true
            loadData()
        }

    }

    /**
     * 初始化 view
     */
    private fun initView() {
        swipeRefreshLayout = findViewById(R.id.xp_log_http_activity_swiperefreshlayout)
        recyclerView = findViewById(R.id.xp_log_http_activity_recyclerview)
        tvFilter = findViewById(R.id.xp_log_http_activity_filter)
        tvTitle = findViewById(R.id.xp_log_http_activity_tvtitle)
        tvCount = findViewById(R.id.xp_log_http_activity_tvcount)
        ivLogo = findViewById(R.id.xp_log_http_activity_ivlogo)
        etKeyword = findViewById(R.id.xp_log_http_activity_etsearch)
        btnNext = findViewById(R.id.xp_log_http_activity_tvnext)
        btnPrevious = findViewById(R.id.xp_log_http_activity_tvprevious)



        mLogAdapter = LogAdapter(this, mMainScope) {
            loadMoreData()
        }
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = mLogAdapter
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        try {
            this.applicationContext?.packageManager?.run {
                this.getApplicationIcon(packageName)?.let {
                    ivLogo?.setImageDrawable(it)
                }
                this.getApplicationInfo(packageName, 0)?.let {
                    val string = resources.getString(it.labelRes)
                    tvTitle?.text = string
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 设置监听器
     */
    private fun setListener() {
        swipeRefreshLayout?.setOnRefreshListener {
            loadData()
        }

        //下一个
        btnNext?.setOnClickListener {
            val trim = etKeyword?.text.toString().trim()

            if (TextUtils.isEmpty(mLogAdapter?.keyword)) {
                if (!TextUtils.isEmpty(trim)) {
                    mLogAdapter?.processKeyWord(trim)
                }
            } else {
                if (trim.equals(mLogAdapter?.keyword, true)) {
                    //下一个
                    mLogAdapter?.nextKeyWord()
                } else {
                    mLogAdapter?.processKeyWord(trim)
                }
            }


        }
        //上一个
        btnPrevious?.setOnClickListener {

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
                } else {
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
            tvCount?.text =
                "T:$logCounts, s:$startIndex, c:${mLogAdapter?.list?.filter { it.itemType == ListData.ITEM_TYPE }?.size}"
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
            tvCount?.text =
                "T:$logCounts, s:$startIndex, c:${mLogAdapter?.list?.filter { it.itemType == ListData.ITEM_TYPE }?.size}"
        }


    }

    override fun onDestroy() {
        jobLoading?.cancel()
        jobRefresh?.cancel()
        super.onDestroy()
    }
}
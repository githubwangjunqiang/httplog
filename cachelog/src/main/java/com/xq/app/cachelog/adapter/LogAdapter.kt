package com.xq.app.cachelog.adapter

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xq.app.cachelog.R
import com.xq.app.cachelog.entiy.ListData
import com.xq.app.cachelog.utils.format
import com.xq.app.cachelog.utils.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @data 2021/4/1
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
class LogAdapter(
    val context: Context,
    var mCoroutineScope: CoroutineScope,
    val loading: (() -> Unit)? = null
) :
    RecyclerView.Adapter<LogBaseAdapter>() {
    private var filter = mutableListOf<ListData>()
    private var launchSearch: Job? = null
    private var recyclerView: RecyclerView? = null
    private val layoutInflater = LayoutInflater.from(context)
    val list = mutableListOf<ListData>()
    var loadingStats = false

    /**
     * 输入关键词
     */
    var keyword: String? = null

    /**
     * 关键词开始下标 点击下一个 或者上一个的时候 依靠此值
     */
    var indexKerWord = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogBaseAdapter {
        return when (viewType) {
            ListData.ITEM_TYPE -> LogViewHolder(
                layoutInflater.inflate(
                    R.layout.item_log_list,
                    parent,
                    false
                ), filter
            )
            else -> loadingViewHolder(
                layoutInflater.inflate(
                    R.layout.item_log_list_loading,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].itemType
    }

    override fun onBindViewHolder(holder: LogBaseAdapter, position: Int) {
        holder.setData(listData = list[position], position, loading)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val canScrollVertically = recyclerView.canScrollVertically(1)
                        if (!canScrollVertically && !loadingStats) {
                            loadingStats = true
                            loading?.invoke()
                        }
                    }

                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    /**
     * 下一个
     */
    fun nextKeyWord() {
        mCoroutineScope.launch(Dispatchers.Main) {
            if (filter.isEmpty()) {
                "没有搜到关键词".show()
                return@launch
            }
            indexKerWord++
            if (indexKerWord >= filter.size) {
                indexKerWord = filter.size - 1
                "已经是最后一个了".show()
            } else {
                val listData = filter[indexKerWord]
                val indexOf = list.indexOf(listData)
                if (indexOf == -1) {
                    "没有找到位置".show()
                    return@launch
                }
                recyclerView?.let {
                    it.layoutManager?.run {
                        val linearLayoutManager = this as LinearLayoutManager
                        linearLayoutManager.scrollToPositionWithOffset(indexOf, 0)
                        linearLayoutManager
                    }
                }
//                recyclerView?.smoothScrollToPosition(indexOf)
                notifyItemChanged(indexOf)
            }

        }


    }

    /**
     * 上一个
     */
    fun previousKeyWord() {
        mCoroutineScope.launch(Dispatchers.Main) {
            if (filter.isEmpty()) {
                "没有搜到关键词".show()
                return@launch
            }
            indexKerWord--
            if (indexKerWord < 0) {
                indexKerWord = 0
                "已经是第一个了".show()
            } else {
                val indexOf = list.indexOf(filter[indexKerWord])
                if (indexOf == -1) {
                    "没有找到位置".show()
                    return@launch
                }
                recyclerView?.let {
                    it.layoutManager?.run {
                        val linearLayoutManager = this as LinearLayoutManager
                        linearLayoutManager.scrollToPositionWithOffset(indexOf, 0)
                        linearLayoutManager
                    }
                }
//                recyclerView?.smoothScrollToPosition(indexOf)
                notifyItemChanged(indexOf)
            }

        }


    }

    /**
     * 设置关键词
     */
    fun processKeyWord(trim: String) {
        filter?.clear()
        notifyDataSetChanged()
        indexKerWord = -1
        keyword = trim
        if (TextUtils.isEmpty(trim)) {
            "关键词为空".show()
            return
        }
        launchSearch?.cancel()
        launchSearch = mCoroutineScope.launch(Dispatchers.IO) {
            list.forEach {
                if (it.itemType == ListData.ITEM_TYPE) {
                    it.data?.run {
                        val contains = this.url?.contains(keyword!!, true) ?: false
                        if (contains) {
                            filter.add(it)
                        }
                    }
                }
            }
            if (filter.isNullOrEmpty()) {
                "没有搜到关键词".show()
                return@launch
            }

            nextKeyWord()
        }
    }


}

open class LogBaseAdapter : RecyclerView.ViewHolder {
    constructor(view: View) : super(view) {

    }

    open fun setData(listData: ListData, position: Int, loading: (() -> Unit)? = null) {

    }
}

class LogViewHolder(view: View, val filter: MutableList<ListData>) : LogBaseAdapter(view) {

    val tvTitle: TextView = view.findViewById(R.id.item_tvtitle)
    val tvContent: TextView = view.findViewById(R.id.item_tvcontent)
    override fun setData(listData: ListData, position: Int, loading: (() -> Unit)?) {
        super.setData(listData, position, loading)
        tvTitle.setTextColor(
            if (filter.contains(listData)) {
                Color.RED
            } else {
                Color.parseColor("#222222")
            }
        )
        tvTitle.text = "${position + 1} - 接口时间：${listData.data?.logId.format()}"
        tvContent.text = "URL：${listData.data?.url}"
        tvContent.append("\n")
        tvContent.append("userId：${listData.data?.userId}")
        tvContent.append("\n")
        tvContent.append("请求头：${listData.data?.sendHead}")
        tvContent.append("\n")
        tvContent.append("请求参数：${listData.data?.sendParameter}")
        tvContent.append("\n")
        tvContent.append("耗时：${listData.data?.durration} ms")
        tvContent.append("\n")
        tvContent.append("http状态码：${listData.data?.returnHttpCode}")
        tvContent.append("\n")
        tvContent.append("返回头：${listData.data?.returnHeader}")
        tvContent.append("\n")
        tvContent.append("返回体：${listData.data?.returnString}")
        tvContent.append("\n")
        tvContent.append("自定义消息：${listData.data?.customMessage}")


    }
}

class loadingViewHolder : LogBaseAdapter {

    constructor(view: View) : super(view) {

    }

    override fun setData(listData: ListData, position: Int, loading: (() -> Unit)?) {
        super.setData(listData, position, loading)
    }
}
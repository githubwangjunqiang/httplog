package com.xq.app.cachelog.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xq.app.cachelog.R
import com.xq.app.cachelog.entiy.ListData
import com.xq.app.cachelog.utils.format

/**
 * @data 2021/4/1
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
class LogAdapter(val context: Context, val loading: (() -> Unit)? = null) :
    RecyclerView.Adapter<LogBaseAdapter>() {
    private val layoutInflater = LayoutInflater.from(context)
    val list = mutableListOf<ListData>()
    var loadingStats = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogBaseAdapter {
        return when (viewType) {
            ListData.ITEM_TYPE -> LogViewHolder(
                layoutInflater.inflate(
                    R.layout.item_log_list,
                    parent,
                    false
                )
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
        val scrollState = recyclerView.scrollState
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE->{
                        val canScrollVertically = recyclerView.canScrollVertically(1)
                        if (!canScrollVertically  && !loadingStats) {
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


}

open class LogBaseAdapter : RecyclerView.ViewHolder {
    constructor(view: View) : super(view) {

    }

    open fun setData(listData: ListData, position: Int, loading: (() -> Unit)? = null) {

    }
}

class LogViewHolder(val view: View) : LogBaseAdapter(view) {

    val tvContent: TextView = view.findViewById(R.id.item_tvcontent)
    override fun setData(listData: ListData, position: Int, loading: (() -> Unit)?) {
        super.setData(listData, position, loading)
        tvContent.text = "接口存入时间：${listData.data?.logId.format()}"
        tvContent.append("\n")
        tvContent.append("URL：${listData.data?.url}")
        tvContent.append("\n")
        tvContent.append("请求时间：${listData.data?.startTime.format()}")
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
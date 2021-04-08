package com.example.logcache

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ContentProviderActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: AdapterP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_provider)

        recyclerView = findViewById(R.id.recyleview)
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            )
        )
        adapter = AdapterP(this)
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            loadData()
        }

    }

    private fun loadData() {

        val list = arrayListOf<String>()
        val query = contentResolver.query(
            Uri.parse("content://com.app.xq.gamcamp/work"),
            null, null, null, null
        )
        Log.d("12345", "loadData: $query")
        if (query?.moveToLast() == true) {
            val string = query.getString(0)
            list.add(string)
            while (query.moveToPrevious()) {
                val string = query.getString(0)
                list.add(string)
                Log.d("12345", "loadData2: $string")
            }
        }else{

        }
        query?.close()
        adapter.list.clear()
        adapter.list.addAll(list)
        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }
}

class AdapterP(private val context: Context) : RecyclerView.Adapter<ViewHolderProvider>() {

    val list = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderProvider {

        return ViewHolderProvider(
            LayoutInflater.from(context).inflate(R.layout.itemlist, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolderProvider, position: Int) {
        holder.setData(list[position])

    }

    override fun getItemCount(): Int {
        return list.size

    }

}

class ViewHolderProvider(private val view: View) : RecyclerView.ViewHolder(view) {


    val tv: TextView = view.findViewById(R.id.item_tvmsg)
    fun setData(string: String?) {
        tv.text = string
    }

}
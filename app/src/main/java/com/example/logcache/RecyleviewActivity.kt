package com.example.logcache

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mcxtzhang.layoutmanager.swipecard.CardConfig
import com.mcxtzhang.layoutmanager.swipecard.OverLayCardLayoutManager
import com.mcxtzhang.layoutmanager.swipecard.RenRenCallback

class RecyleviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyleview)


        val recyclerView = findViewById<RecyclerView>(R.id.revldouiro)
        recyclerView.layoutManager = OverLayCardLayoutManager().apply {
        }
        val apply = MyAdapter(this).apply {
            for (index in 0..20) {
                list.add("$index")
            }
        }
        recyclerView.adapter = apply
        //初始化配置
        CardConfig.initConfig(this)
        val callback: ItemTouchHelper.Callback = RenRenCallback(recyclerView, apply, apply.list)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter?.notifyDataSetChanged()

    }

    private class MyAdapter(val context: Context) : RecyclerView.Adapter<MyHolder>() {


        val list = arrayListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(LayoutInflater.from(context).inflate(R.layout.item_rev, parent, false))
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.textView.text = "${list[holder.layoutPosition]}"
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    private class MyHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.item_tv)
    }
}
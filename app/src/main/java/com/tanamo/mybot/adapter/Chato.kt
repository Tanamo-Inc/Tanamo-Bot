package com.tanamo.mybot.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tanamo.mybot.R
import com.tanamo.mybot.model.Messo
import java.util.*


class Chato(private val lise: ArrayList<Messo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val self = 100

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = if (viewType == self) {
            // self message
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item_self, parent, false)
        } else {
            // WatBot message
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item, parent, false)
        }

        // view type is to identify where to render the chat message
        // left or right


        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = lise[position]
        return if (message.id == "1") {
            self
        } else position

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = lise[position]
        message.message = message.message
        (holder as ViewHolder).message.text = message.message
    }

    override fun getItemCount(): Int {
        return lise.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var message: TextView = itemView.findViewById(R.id.message)

    }


}
package com.tanamo.mybot.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tanamo.mybot.R
import com.tanamo.mybot.model.Messo
import kotlinx.android.synthetic.main.watson_item.view.*
import java.util.*


class Chato(private val lise: ArrayList<Messo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val self = 60

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = if (viewType == self) {
            // self message
            LayoutInflater.from(parent.context).inflate(R.layout.self_item, parent, false)
        } else {
            // WatBot message
            LayoutInflater.from(parent.context).inflate(R.layout.watson_item, parent, false)
        }

        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = lise[position]
        return if (message.id == "50") {
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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView = itemView.message

    }


}
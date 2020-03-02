package com.android.abhishekvats.gupshup

import android.content.res.Resources
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.my_text_view.view.*
import java.text.DateFormat
import java.util.*


class MyAdapter(list:ArrayList<ChatMessage>):RecyclerView.Adapter<CustomViewHolder>(){

    var list=list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var layoutInflater=LayoutInflater.from(parent.context)
        var bubble=layoutInflater.inflate(R.layout.my_text_view,parent,false)
        return CustomViewHolder(bubble)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        var textMsg=list[position].text
        var type=list[position].type
        var time=DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(list[position].time))

        var layout=holder.itemView.msg
        var params= RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)

        holder.itemView.text.text=textMsg
        holder.itemView.timestamp.text=time
//        holder.itemView.timestamp=list[position].time
        if(type==ChatMessage.SENT){

            layout.setBackgroundResource(R.drawable.bubble_background_send)
            layout.setPadding(20,20,20,20)
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.setMargins(5,5,10,5)
            layout.layoutParams=params
            holder.itemView.text.setTextColor(Color.parseColor("#000000"))
            holder.itemView.timestamp.setTextColor(Color.parseColor("#000000"))
        }
        else{
            holder.itemView.msg.setBackgroundResource(R.drawable.bubble_background)
            holder.itemView.text.setTextColor(Color.parseColor("#ffffff"))
            holder.itemView.timestamp.setTextColor(Color.parseColor("#ffffff"))
            params.setMargins(10,5,5,5)
            layout.layoutParams=params
            layout.setPadding(20,20,20,20)
        }
    }

}

class CustomViewHolder(view:View):RecyclerView.ViewHolder(view){

}
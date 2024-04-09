package com.example.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.NotesData
import com.example.notes.R

class MyAdapter(
    private val userList: ArrayList<NotesData>,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val onUpdateClickListener: OnUpdateClickListener
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    interface OnUpdateClickListener {
        fun onUpdateClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.tvFirstName.text = currentItem.title
        holder.tvLastName.text = currentItem.content

        holder.imageViewDelete.setOnClickListener {
            onDeleteClickListener.onDeleteClick(position)
        }

        holder.imageViewUpdate.setOnClickListener {
            onUpdateClickListener.onUpdateClick(position)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFirstName: TextView = itemView.findViewById(R.id.tvfirstName)
        val tvLastName: TextView = itemView.findViewById(R.id.tvlastName)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)
        val imageViewUpdate: ImageView = itemView.findViewById(R.id.imageViewUpdate)
    }
}


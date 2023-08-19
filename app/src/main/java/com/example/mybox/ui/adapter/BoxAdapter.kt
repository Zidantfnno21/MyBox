package com.example.mybox.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.example.mybox.data.model.CategoryModel

class BoxAdapter(private val listBox: ArrayList<CategoryModel>) : RecyclerView.Adapter<BoxAdapter.ListViewHolder>() {

    class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: View = itemView.findViewById(R.id.ivRv)
        val tvName: TextView = itemView.findViewById(R.id.textViewCard)
        val tvDescription: TextView = itemView.findViewById(R.id.textViewCategoryCard)
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_card, parent ,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder : ListViewHolder , position : Int) {
        val (Id , Name , Description, ImageURL) = listBox[position]
        holder.tvName.text = Name
        holder.tvDescription.text = Description
        holder.itemView.setOnClickListener{

        }


    }

    override fun getItemCount() : Int = listBox.size

}


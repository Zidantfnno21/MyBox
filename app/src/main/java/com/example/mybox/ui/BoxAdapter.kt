package com.example.mybox.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.squareup.picasso.Picasso

class BoxAdapter(private val boxList: List<CategoryModel>, private val adapterOnClick: (CategoryModel) -> Unit) : RecyclerView.Adapter<BoxAdapter.BoxrHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): BoxrHolder {
        return BoxrHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_card , viewGroup , false))
    }

    override fun getItemCount(): Int = boxList.size

    override fun onBindViewHolder(holder: BoxrHolder , position: Int) {
        holder.bindHero(boxList[position])
    }

    inner class BoxrHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindHero(boxr: CategoryModel) {
            itemView.apply {
                var itemImage : ImageView = itemView.findViewById(R.id.ivRv)
                var itemTitle : TextView = itemView.findViewById(R.id.textViewCard)
                var itemCategory : TextView = itemView.findViewById(R.id.textViewCategoryCard)
                Picasso.get().load(boxr.ImageURL).into(itemImage)

                setOnClickListener {
                    adapterOnClick(boxr)
                }
            }
        }
    }
}


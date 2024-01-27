package com.example.mybox.ui.detailScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybox.R
import com.example.mybox.data.model.DetailModel
import com.example.mybox.utils.convertTimestampToISOString

class CategoryDetailAdapter(
    private val onClick: (DetailModel) -> Unit
) : ListAdapter<DetailModel, CategoryDetailAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val picture: View = itemView.findViewById(R.id.iVItem)
        private val title: TextView = itemView.findViewById(R.id.tVItemName)
        private val timeStamp: TextView = itemView.findViewById(R.id.tVItemTimeStamp)

        lateinit var getDetailBox: DetailModel

        fun bind(item: DetailModel){
            title.text = item.name
            timeStamp.text = item.timeStamp?.let { convertTimestampToISOString(it) }
            Glide.with(itemView.context)
                .load(item.imageURL)
                .into(picture as ImageView)

            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) =
        ListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_card, parent, false)
        )


    override fun onBindViewHolder(holder : ListViewHolder , position : Int) {
        val detailBox = getItem(position)
        if (detailBox != null) {
            holder.bind(detailBox)
            holder.getDetailBox = detailBox
        }
    }

    fun removeItem(position: Int) {
        val currentList = ArrayList(currentList)
        currentList.removeAt(position)
        submitList(currentList)
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailModel>() {
            override fun areItemsTheSame(oldItem: DetailModel , newItem: DetailModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DetailModel , newItem: DetailModel): Boolean {
                return oldItem == newItem
            }
        }

    }
}
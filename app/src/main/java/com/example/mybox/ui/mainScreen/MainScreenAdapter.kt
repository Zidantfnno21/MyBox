package com.example.mybox.ui.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybox.R
import com.example.mybox.data.model.CategoryModel

class MainScreenAdapter(
    private val onClick: (CategoryModel) -> Unit
) : androidx.recyclerview.widget.ListAdapter<CategoryModel, MainScreenAdapter.ListViewHolder>(
    DIFF_CALLBACK) {

    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val imgPhoto: View = itemView.findViewById(R.id.ivRv)
        private val tvName: TextView = itemView.findViewById(R.id.textViewCard)
        private val tvDescription: TextView = itemView.findViewById(R.id.textViewCategoryCard)

        lateinit var getBox: CategoryModel
        fun bind(box : CategoryModel) {
            tvName.text = box.Name
            tvDescription.text = box.Description
            Glide.with(itemView.context)
                .load(box.ImageURL)
                .into(imgPhoto as ImageView)

            itemView.setOnClickListener {
                onClick(box)
            }
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) =
        ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_card, parent, false))

    override fun onBindViewHolder(holder : ListViewHolder , position : Int) {
        val box = getItem(position)
        if (box != null) {
            holder.bind(box)
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryModel>() {
            override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem.Id == newItem.Id
            }

            override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem == newItem
            }
        }

    }

}


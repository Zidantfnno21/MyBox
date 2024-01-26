package com.example.mybox.ui.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybox.R
import com.example.mybox.data.model.CategoryModel

class MainScreenAdapter(
    private val onClick: (CategoryModel) -> Unit,
    private val onButtonClick: (CategoryModel, position: Int) -> Unit
) : androidx.recyclerview.widget.ListAdapter<CategoryModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    var viewState = 0

    override fun getItemViewType(position : Int) : Int {
        return viewState
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                ViewHolderWithImage(LayoutInflater.from(parent.context).inflate(R.layout.item_card_view, parent, false))
            }
            else -> {
                ViewHolderOnlyText(LayoutInflater.from(parent.context).inflate(R.layout.list_card_text, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder : RecyclerView.ViewHolder , position : Int) {
        when(holder){
            is ViewHolderWithImage -> {
                val box = getItem(position)
                if (box != null) {
                    holder.bind(box)
                    holder.getBox = box
                }
            }
            is ViewHolderOnlyText -> {
                val box = getItem(position)
                if (box != null) {
                    holder.bind(box)
                    holder.getBox = box
                }
            }

        }

    }

    inner class ViewHolderWithImage(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val imgPhoto: ImageView = itemView.findViewById(R.id.ivRv)
        private val tvName: TextView = itemView.findViewById(R.id.textViewCard)
        private val tvDescription: TextView = itemView.findViewById(R.id.textViewCategoryCard)
        private val btOption: Button = itemView.findViewById(R.id.imageButton)

        lateinit var getBox: CategoryModel

        fun bind(box : CategoryModel) {
            tvName.text = box.name
            tvDescription.text = box.description
            Glide.with(itemView.context)
                .load(box.imageURL)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgPhoto)

            itemView.setOnClickListener {
                onClick(box)
            }

            btOption.setOnClickListener {
                onButtonClick(box, bindingAdapterPosition)
            }
        }
    }

    inner class ViewHolderOnlyText(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.textViewCard)
        private val tvDescription: TextView = itemView.findViewById(R.id.textViewCategoryCard)
        private val btOption: Button = itemView.findViewById(R.id.imageButton)

        lateinit var getBox: CategoryModel
        fun bind(box : CategoryModel) {
            tvName.text = box.name
            tvDescription.text = box.description

            itemView.setOnClickListener {
                onClick(box)
            }

            btOption.setOnClickListener {
                onButtonClick(box, bindingAdapterPosition)
            }
        }
    }

    fun removeItem(position: Int) {
        val currentList = ArrayList(currentList)
        currentList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
        submitList(currentList)
    }

    var fullList: List<CategoryModel> = listOf()

    fun submitFullList(list: List<CategoryModel>) {
        fullList = list
        submitList(fullList)
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint : CharSequence?) : FilterResults {
                val results = FilterResults()
                results.values = if (constraint.isNullOrBlank()) {
                    fullList
                } else {
                    fullList.filter { item ->
                        item.name?.contains(constraint , ignoreCase = true) == true ||
                                item.description?.contains(constraint , ignoreCase = true) == true
                    }
                }
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint : CharSequence? , results : FilterResults?) {
                val filteredList = results?.values as? List<CategoryModel> ?: emptyList()
                submitList(filteredList)
            }

        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryModel>() {
            override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
                return oldItem == newItem
            }
        }

    }
}


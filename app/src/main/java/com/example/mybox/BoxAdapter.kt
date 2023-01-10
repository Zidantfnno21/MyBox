package com.example.mybox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BoxAdapter(private val heroes: List<CategoryModel>,
                 private val adapterOnClick: (CategoryModel) -> Unit) : RecyclerView.Adapter<BoxAdapter.BoxrHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): BoxrHolder {
        return BoxrHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_card, viewGroup, false))
    }

    override fun getItemCount(): Int = heroes.size

    override fun onBindViewHolder(holder: BoxrHolder, position: Int) {
        holder.bindHero(heroes[position])
    }

    inner class BoxrHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        fun bindHero(boxr: CategoryModel) {
            itemView.apply {
                txtHeroName.text = boxr.Name
                Picasso.get().load(boxr.ImageURL).into(imgHeroes)

                setOnClickListener {
                    adapterOnClick(boxr)
                }
            }
        }
    }
}


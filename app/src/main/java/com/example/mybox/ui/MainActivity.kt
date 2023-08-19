package com.example.mybox.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.example.mybox.data.model.CategoryModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private val list = ArrayList<CategoryModel>()
    private lateinit var dbReference: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingActionButton = findViewById(R.id.fAB1)

        recyclerView = findViewById(R.id.rvMainPage)
        recyclerView.setHasFixedSize(true)

        list.addAll(getListBox())
        showRecyclerList()

        floatingActionButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))

        }
    }

    private fun showRecyclerList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val listBoxAdapter =

    }

    private fun getListBox() : ArrayList<CategoryModel> {
        val dataName = resources.getStringArray()
        val dataDescription = resources.getStringArray()
        val listBox = ArrayList<CategoryModel>()
        for (i in dataName.indices) {
            val box = CategoryModel
        }
        return listBox
    }
}

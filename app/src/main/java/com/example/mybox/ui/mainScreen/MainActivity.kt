package com.example.mybox.ui.mainScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybox.R
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.ui.SecondActivity
import com.example.mybox.ui.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private val list = ArrayList<CategoryModel>()
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingActionButton = findViewById(R.id.fAB1)

        recyclerView = findViewById(R.id.rvMainPage)
        recyclerView.setHasFixedSize(true)

        floatingActionButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))

        }
    }
//
//    private fun showRecyclerList() {
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        val listBoxAdapter =
//
//    }
//
//    private fun getListBox() : ArrayList<CategoryModel> {
//        val dataName = resources.getStringArray()
//        val dataDescription = resources.getStringArray()
//        val listBox = ArrayList<CategoryModel>()
//        for (i in dataName.indices) {
//            val box = CategoryModel
//        }
//        return listBox
//    }
}

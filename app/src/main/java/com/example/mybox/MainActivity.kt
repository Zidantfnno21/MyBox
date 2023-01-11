package com.example.mybox

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var boxList: ArrayList<CategoryModel>
    private lateinit var dbReference: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        floatingActionButton = findViewById(R.id.fAB1)
        recyclerView = findViewById(R.id.rvMainPage)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)


        val boxAdapter = BoxAdapter(boxList) { boxr ->
            Toast.makeText(applicationContext, "hehe", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = boxAdapter

        floatingActionButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))

        }
    }
}

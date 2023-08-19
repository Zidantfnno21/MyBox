package com.example.mybox.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.mybox.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InsideActivity : AppCompatActivity() {
    lateinit var backButton: ImageButton
    lateinit var optionButton: ImageButton
    lateinit var floatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inside)
        floatingActionButton = findViewById(R.id.floatingActionButton2)
        optionButton = findViewById(R.id.iB6)
        backButton = findViewById(R.id.btImageBack)

        backButton.setOnClickListener{
            val intent = Intent(this@InsideActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        floatingActionButton.setOnClickListener{
            val intent = Intent(this@InsideActivity, ItemActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package com.example.mybox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddActivity : AppCompatActivity() {
    private lateinit var etCategory: TextInputEditText
    private lateinit var btnBack: Button
    private lateinit var btnSave: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etCategory = findViewById(R.id.tICategory)
        btnBack = findViewById(R.id.btBack)
        btnSave = findViewById(R.id.btSave)
        database = FirebaseDatabase.getInstance()


        btnSave.setOnClickListener {
            saveCategoryData()
        }
        btnBack.setOnClickListener {
            val intent = Intent(this@AddActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun saveCategoryData() {
        val storageName = etCategory.text.toString()
        if (storageName.isEmpty()){
            val toast = Toast.makeText(this, "Please fill out the form", Toast.LENGTH_SHORT)
            toast.show()
        }
            val categoryId = dbReference.push().key!!
            val category = CategoryModel(categoryId, storageName)
            dbReference.child(categoryId).setValue(category)
                .addOnCompleteListener {
                    Toast.makeText(this, "Make Storage Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddActivity, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error Make Storage :(", Toast.LENGTH_SHORT).show()
                }

    }
}
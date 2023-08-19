package com.example.mybox

import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.*
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.mybox.data.model.CategoryModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SecondActivity : AppCompatActivity() {
    private lateinit var etCategory : TextInputEditText
    private lateinit var etDescription : TextInputEditText
    private lateinit var btChangePic : TextView
    private lateinit var btSave : Button
    private lateinit var btBack : Button
    private lateinit var ivCategory : ImageView
    private lateinit var dbReference : DatabaseReference
    private lateinit var StorageReference : StorageReference
    private lateinit var imageUri : Uri
    private lateinit var LinearLayout : LinearLayout

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add2)
        btChangePic = findViewById(R.id.btChangePic)
        etCategory = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDesc)
        btBack = findViewById(R.id.buttonBack)
        btSave = findViewById(R.id.buttonSave)
        ivCategory = findViewById(R.id.profilePicture3)
        dbReference = FirebaseDatabase.getInstance().getReference("Category")
        StorageReference = FirebaseStorage.getInstance().reference.child("Image Category").child(
            Date().time.toString()
        )
        LinearLayout = findViewById(R.id.SHOW_PROGRESS)


        btSave.setOnClickListener {

            saveCategoryData()
        }
        btBack.setOnClickListener {
            LinearLayout.visibility = View.VISIBLE
            val intent = Intent(this@SecondActivity , MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btChangePic.setOnClickListener {
            chooseAction()
        }
    }

    private fun chooseAction() {
        val options = arrayOf<CharSequence>("Take a photo", "Choose from gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { _ , which ->
            when(which){
                0 -> {
                    takePictureFromCamera()
                }
                1 -> {
                    choosePhotoFromGallary()
                }
            }
        }
        builder.show()
    }

    private fun choosePhotoFromGallary() {
        val openGallery = Intent(Intent.ACTION_PICK , Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGallery , 321)
    }



    private fun takePictureFromCamera() {
        val openCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(openCamera, 123)

    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent? ) {
        super.onActivityResult(requestCode , resultCode , data)
        when(requestCode){
            321 -> {
                if (data != null) {
                    imageUri = data.data!!
                    try {
                        val bitmap =
                            Images.Media.getBitmap(this.contentResolver , imageUri) as Bitmap
                        ivCategory.setImageBitmap(bitmap)
                    } catch (e : IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@SecondActivity , "Failed!" , Toast.LENGTH_SHORT).show()
                    }
                }
            }
            123 -> {
                ivCategory.setImageBitmap(data?.extras?.get("data") as Bitmap)
            }
        }
    }

    private fun createImageFile() : File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss" , Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_" , ".jpg" , storageDir)

    }

    private fun saveCategoryData() {
        StorageReference.putFile(imageUri)
            .addOnSuccessListener {
                StorageReference.downloadUrl.addOnCompleteListener{
                    val storageName = etCategory.text.toString()
                    val storageDesc = etDescription.text.toString()
                    val categoryId = dbReference.push().key !!
                    val category = CategoryModel(categoryId , storageName , storageDesc , imageUri.toString())
                    dbReference.child(categoryId).setValue(category).addOnCompleteListener {
                        Toast.makeText(applicationContext, "Berhasil cok", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(applicationContext, "Gabisa cok", Toast.LENGTH_SHORT).show()
                    }
                }
                LinearLayout.visibility = View.GONE
                Toast.makeText(applicationContext , "Image Uploaded" , Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@SecondActivity , InsideActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(applicationContext , "gabisa ini mas" , Toast.LENGTH_SHORT)
                    .show()
            }
    }

}


package com.example.mybox


import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.util.*


class SecondActivity : AppCompatActivity() {
    private lateinit var etCategory: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btChangePic: TextView
    private lateinit var btSave: Button
    private lateinit var btBack: Button
    private lateinit var ivCategory: ImageView
    private lateinit var dbReference: DatabaseReference
    private lateinit var StorageReference : StorageReference
    private lateinit var Uri : Uri
    private lateinit var LinearLayout : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add2)
        btChangePic = findViewById(R.id.btChangePic)
        etCategory = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDesc)
        btBack = findViewById(R.id.buttonBack)
        btSave = findViewById(R.id.buttonSave)
        ivCategory = findViewById(R.id.profilePicture3)
        dbReference = FirebaseDatabase.getInstance().getReference("Category")
        StorageReference = FirebaseStorage.getInstance().getReference("/ImagesCategory/")
        LinearLayout = findViewById(R.id.SHOW_PROGRESS)


        btSave.setOnClickListener {
            saveCategoryData()
        }
        btBack.setOnClickListener {
            LinearLayout.visibility = View.VISIBLE
            val intent = Intent(this@SecondActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btChangePic.setOnClickListener {
            showPictureDialog()
        }
    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Take From Camera", "Select From Gallery")
        pictureDialog.setItems(pictureDialogItems){
                dialog, which ->
            when (which) {
                1 -> choosePhotoFromGallary()
                0 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Pic")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera")
        Uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val openCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri)
        startActivityForResult(openCamera, 123)
    }

    private fun choosePhotoFromGallary() {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGallery, 321)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 321){
            if (data != null){
                val Uri = data.data!!
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri) as Bitmap
                    ivCategory.setImageBitmap(bitmap)
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this@SecondActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }

        }else
        if (requestCode == 123){
            if (data != null){
                data.data
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri) as Bitmap
                ivCategory.setImageBitmap(bitmap)
            }

            //var bitmapDrawable = BitmapDrawable(bitmap)

        }
    }


    private fun saveCategoryData() {
        val storageName = etCategory.text.toString()
        val storageDesc = etDescription.text.toString()
        if (storageName.isEmpty()){
            etCategory.setError("Gaboleh Kosong Maniest")
        }else if(storageName.length < 8){
            etCategory.setError("Minimal 8 Char blokkk")
        }
        if (storageDesc.isEmpty()){
            etDescription.setError("Gaboleh Kosong ya Maniest")
        }else if (storageDesc.length < 8){
            etDescription.setError("Minimal 8 Char sayangg ^^")
        }
        if(ivCategory.drawable == null){
            Toast.makeText(applicationContext, "Category Picture cannot be empty too :(", Toast.LENGTH_SHORT).show()
        }
        else{
            val categoryId = dbReference.push().key!!
            val category = CategoryModel(categoryId, storageName, storageDesc)
            dbReference.child(categoryId).setValue(category)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "Make Storage Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SecondActivity, InsideActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error Make Storage :(", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /*private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = externalCacheDir
        if (getImage != null) {
            outputFileUri = android.net.Uri.fromFile(File(getImage.getPath(), "profile.png"))
        }
        return outputFileUri*/
}
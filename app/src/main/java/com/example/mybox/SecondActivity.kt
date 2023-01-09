package com.example.mybox

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    private var imageUri : Uri? = null
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
        StorageReference = FirebaseStorage.getInstance().getReference("ImageCategory")
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
            choosePhotoFromGallary()
        }
    }

    private fun choosePhotoFromGallary() {
        val openGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGallery , 321)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)

        if (requestCode == 321) {
            if (data != null) {
                imageUri = data.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver , imageUri) as Bitmap
                    ivCategory.setImageBitmap(bitmap)
                } catch (e : IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@SecondActivity , "Failed!" , Toast.LENGTH_SHORT).show()
                }
            }

        }
        if (requestCode == 123 && resultCode == RESULT_OK) {
            if (data != null) {
                //val values = ContentValues()
                //values.put(MediaStore.Images.Media.TITLE, "New Pic")
                //values.put(MediaStore.Images.Media.DESCRIPTION, "Camera")
                //imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!


                imageUri = data.data
                try {
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver , imageUri)
                    ivCategory.setImageBitmap(imageBitmap)

                } catch (e : IOException) {

                }


                //val imageFile = createImageFile()
                //val outputStream = FileOutputStream(imageUri)
                //imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                //ivCategory.setImageBitmap(imageBitmap)
                //


                //val bitmap: Bitmap = data.extras?.get("data") as Bitmap

                //bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
                //val file : String = Base64.getEncoder().toString()
                //val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                //val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri) as Bitmap
                //ivCategory.setImageBitmap(bitmap)


            }

            //var bitmapDrawable = BitmapDrawable(bitmap)

        }
    }

    private fun createImageFile() : File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss" , Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName , ".jpg" , storageDir)
    }

    private fun saveCategoryData() {

        //val category = CategoryModel(categoryId, storageName, storageDesc)
        if (imageUri != null) {
            val categoryID = UUID.randomUUID().toString()
            StorageReference.child(categoryID).putFile(imageUri !!)
                //.addOnCompleteListener { LinearLayout.visibility = View.VISIBLE }
                .addOnSuccessListener {


                    //val category   = CategoryModel(categoryId, storageName,storageDesc, linkImage)
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
            StorageReference.child(categoryID).downloadUrl.addOnCompleteListener {



            }


        } else {
            Toast.makeText(applicationContext , "berarti ini gabissa mass" , Toast.LENGTH_SHORT)
                .show()
        }

    }


    private fun saveUser(profileImageUri : String) {
        val storageName = etCategory.text.toString()
        val storageDesc = etDescription.text.toString()
            val categoryId = dbReference.push().key !!
            val category = CategoryModel(categoryId , storageName , storageDesc )
            dbReference.child(categoryId).setValue(category)
                .addOnCompleteListener {
                    Toast.makeText(
                        applicationContext ,
                        "Make Storage Successful" ,
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    Toast.makeText(this , "Error Make Storage :(" , Toast.LENGTH_SHORT).show()
                }
    }

}



// private fun getCaptureImageOutputUri(): Uri? {
// var outputFileUri: Uri? = null
// val getImage: File? = externalCacheDir
// if (getImage != null) {
// outputFileUri = android.net.Uri.fromFile(File(getImage.getPath(), "profile.png"))
// }
// return outputFileUri
// private fun showPictureDialog() {
// val pictureDialog = AlertDialog.Builder(this)
// pictureDialog.setTitle("Select Action")
// val pictureDialogItems = arrayOf("Select From Gallery")
// pictureDialog.setItems(pictureDialogItems){
// dialog, which ->
// when (which) {
// 0 -> choosePhotoFromGallary()
// }
// }
// pictureDialog.show()
// }
//
// //    private fun takePhotoFromCamera() {
// //        val values = ContentValues()
// //        values.put(MediaStore.Images.Media.TITLE, "New Pic")
// //        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera")
// //        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
// //        val openCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
// //        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
// //        startActivityForResult(openCamera, 123)
// //        openCamera.type = "image/*"
// //    }
//
// */
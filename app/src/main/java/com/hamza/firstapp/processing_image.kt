package com.hamza.firstapp

import ImageCompressor
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.compose.foundation.layout.Box as Box1



class processing_image : AppCompatActivity() {

    private val REQUEST_IMAGE_SELECT = 100
    val REQUEST_CODE_CAMERA_PERMISSION = 101
    val REQUEST_CODE_CAMERA_CAPTURE = 102
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imageView: ImageView
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView

    var urlImage: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_image)
        val editText = findViewById<EditText>(R.id.editTextText)
        val k = findViewById<EditText>(R.id.editTextText2)

        text1 = findViewById(R.id.textView6)
        text2 =  findViewById(R.id.textView5)
        text3 =  findViewById(R.id.textView7)


        //imageView = findViewById<ImageView>(R.id.imageView)
        imageView = findViewById(R.id.imageView)
        var imageView2: ImageView = findViewById(R.id.imageView2)

        val saveButton = findViewById<Button>(R.id.button3)
        saveButton.setOnClickListener {
            saveImageToGallery(imageView2)
        }




        val radioButton1 = findViewById<RadioButton>(R.id.radioButton)
        val radioButton2 = findViewById<RadioButton>(R.id.radioButton2)
        val radioButton3 = findViewById<RadioButton>(R.id.radioButton3)
        val upload = findViewById<Button>(R.id.button)
        val compress = findViewById<Button>(R.id.button2)

        editText.visibility = View.INVISIBLE

        upload.setOnClickListener {
            val selectedRadioButton: RadioButton? = when {
                radioButton1.isChecked -> radioButton1
                radioButton2.isChecked -> radioButton2
                radioButton3.isChecked -> radioButton3
                else -> null
            }

            if (selectedRadioButton != null) {
                val radioButtonValue = selectedRadioButton.text

                when (selectedRadioButton) {
                    findViewById<RadioButton>(R.id.radioButton) -> {
                        // Find your ImageView
                        imageView = findViewById(R.id.imageView)

                        //val imageView: ImageView = findViewById(R.id.imageView)

                        // URL of the image
                        val urlEditText: EditText = findViewById(R.id.editTextText)

                        //"https://images.freeimages.com/images/large-previews/2ab/dog-1392238.jpg"
                        urlImage = urlEditText.text.toString()
                        // Use Glide to load the image into the ImageView
                        val requestOptions = RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image

                        Glide.with(this)
                            .load(urlImage)
                            .apply(requestOptions)
                            .into(imageView)


                    }
                    findViewById<RadioButton>(R.id.radioButton2) -> {
                        openImageChooser()
                    }
                    findViewById<RadioButton>(R.id.radioButton3) -> {

                        dispatchTakePictureIntent()
                    }
                }

            } else {
                println("No radio button is selected")
            }
        }

        compress.setOnClickListener {


            val k: String =  k.text.toString()
            try {
                if(k .toInt() > 0){
                    val lmyBitmap: Bitmap = ImageUtils.getBitmapFromImageView(imageView)!!
                    text1.setText(ImageUtils.getBitmapSize(lmyBitmap).toString()+" kb")
                    ////////////////////////
                    // Compress the image with a specific rank
                    val compressor = ImageCompressor(lmyBitmap)
                    val compressedBitmap = compressor.compressImage(k = k .toInt())

                    compressor.limitRangeK

                    // Display the compressed image
                    imageView2?.setImageBitmap(compressedBitmap)
                    text2.setText(ImageUtils.getBitmapSize(compressedBitmap).toString()+" kb")

                    text3.setText((ImageUtils.getBitmapSize(lmyBitmap)-ImageUtils.getBitmapSize(compressedBitmap)).toString() +" kb saved!")
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "K value error !", Toast.LENGTH_SHORT).show()
            }
        }


        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            when (radioButton) {
                findViewById<RadioButton>(R.id.radioButton) -> {
                    editText.visibility = View.VISIBLE
                }
                findViewById<RadioButton>(R.id.radioButton2) -> {
                    editText.visibility = View.INVISIBLE
                }
                findViewById<RadioButton>(R.id.radioButton3) -> {
                    editText.visibility = View.INVISIBLE
                }
            }
        }

    }


    private fun saveImageToGallery(imageView: ImageView) {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap

        // Get the content resolver
        val contentResolver = applicationContext.contentResolver

        // Define the file details
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        // Insert the image details into the MediaStore
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDetails)

        // Open an OutputStream to write data into the image file
        val outputStream: OutputStream? = imageUri?.let {
            contentResolver.openOutputStream(it)
        }

        outputStream?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Set the IS_PENDING flag to 0, indicating that the image is complete
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(imageUri!!, imageDetails, null, null)
        }
    }



    private fun FileOutputStream(imageFile: Unit): OutputStream {
        TODO("Not yet implemented")
    }


    private fun openCamera() {
        val values = ContentValues()
        val photoUri: Uri? = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_CODE_CAMERA_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA_CAPTURE && resultCode == RESULT_OK) {
            val photoUri: Uri? = data?.data
            // imageView?.setImageURI(photoUri)
            saveImageToStorage(photoUri)
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)
        }
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data ?: return
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_SELECT)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun saveImageToStorage(photoUri: Uri?) {
        photoUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val file = File(getExternalFilesDir(null), "captured_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }
    }




}
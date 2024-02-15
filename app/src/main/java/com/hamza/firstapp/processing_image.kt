package com.hamza.firstapp

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream

class processing_image : AppCompatActivity() {


    val REQUEST_CODE_CAMERA_PERMISSION = 101
    val REQUEST_CODE_CAMERA_CAPTURE = 102

    var imageView: ImageView? = null
    var imageView2: ImageView? = null
    var urlImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_image)
        val editText = findViewById<EditText>(R.id.editTextText)

        imageView = findViewById<ImageView>(R.id.imageView)
        imageView2 = findViewById<ImageView>(R.id.imageView2)



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
                        val imageView: ImageView = findViewById(R.id.imageView)

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

                    }
                    findViewById<RadioButton>(R.id.radioButton3) -> {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            openCamera()
                        } else {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(android.Manifest.permission.CAMERA),
                                REQUEST_CODE_CAMERA_PERMISSION
                            )
                        }
                    }
                }

            } else {
                println("No radio button is selected")
            }
        }

        compress.setOnClickListener {

            urlImage?.let { it1 ->
                ImageUtils.loadImageFromUrl(this, it1) { bitmap ->
                    // Use the loaded bitmap here
                    if (bitmap != null) {
                        // Do something with the bitmap

                        val greenLayerBitmap: Bitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

                        // Iterate over each pixel of the original bitmap
                        for (x in 0 until bitmap.width) {
                            for (y in 0 until bitmap.height) {
                                // Get the color of the pixel in the original bitmap
                                val color = bitmap.getPixel(x, y)

                                // Extract the green channel value from the color
                                val green = Color.green(color)

                                // Create a new color with only the green channel value
                                val newColor = Color.rgb(0, green, 0)

                                // Set the new color to the corresponding pixel in the green layer bitmap
                                greenLayerBitmap.setPixel(x, y, newColor)
                            }
                        }



                        imageView2?.setImageBitmap(greenLayerBitmap)
                    } else {
                        // Handle failure to load image
                    }
                }
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
            imageView?.setImageURI(photoUri)
            saveImageToStorage(photoUri)
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
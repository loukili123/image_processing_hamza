package com.hamza.firstapp

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import java.io.File
import java.io.FileOutputStream

class processing_image : AppCompatActivity() {


    val REQUEST_CODE_CAMERA_PERMISSION = 101
    val REQUEST_CODE_CAMERA_CAPTURE = 102

    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_image)
        val editText = findViewById<EditText>(R.id.editTextText)

        imageView = findViewById<ImageView>(R.id.imageView)



        val radioButton1 = findViewById<RadioButton>(R.id.radioButton)
        val radioButton2 = findViewById<RadioButton>(R.id.radioButton2)
        val radioButton3 = findViewById<RadioButton>(R.id.radioButton3)
        val upload = findViewById<Button>(R.id.button)

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
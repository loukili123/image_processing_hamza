package com.hamza.firstapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.security.auth.callback.Callback


class firstPage : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var displayTextView: TextView
    private lateinit var displayButton: Button


    private lateinit var imageView: ImageView
    private lateinit var urlImage: EditText
    private lateinit var afficher: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_page)


        val openActivityButton = findViewById<ImageButton>(R.id.imageButton)
        openActivityButton.setOnClickListener {
            val tt = Intent(this, processing_image::class.java)
            startActivity(tt)
        }





    }


}
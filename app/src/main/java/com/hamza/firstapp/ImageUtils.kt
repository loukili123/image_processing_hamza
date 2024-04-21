package com.hamza.firstapp

import Jama.SingularValueDecomposition
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.Color

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

import Jama.Matrix
import org.apache.commons.math3.linear.MatrixUtils


object ImageUtils {



    fun loadImageFromUrl(context: Context, url: String, callback: (Bitmap?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .submit()
                    futureTarget.get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            callback(bitmap)
        }
    }

    fun getBitmapFromImageView(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        return null
    }

    fun getBitmapSize(bitmap: Bitmap): Int {
        // Calculate the size of the Bitmap in bytes
        val byteCount = bitmap.byteCount
        // Convert bytes to kilobytes (1 kilobyte = 1024 bytes)
        val kilobytes = byteCount / 1024
        return kilobytes
    }






}
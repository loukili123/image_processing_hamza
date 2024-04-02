package com.hamza.firstapp

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.Color
import Jama.Matrix
import Jama.SingularValueDecomposition
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.lang.Math.min


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

    fun extractRedChannel(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = pixel shr 16 and 0xFF
                val newPixel = red shl 16 // Keeping the red channel and setting others to 0
                resultBitmap.setPixel(x, y, newPixel)
            }
        }

        return resultBitmap
    }

    fun addRedLayer(originalBitmap: Bitmap): Bitmap {
        // Create a new bitmap with the same dimensions as the original bitmap
        val resultBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)

        // Create a Canvas object to draw on the new bitmap
        val canvas = Canvas(resultBitmap)

        // Draw the original bitmap onto the canvas
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // Create a Paint object for drawing the red layer
        val paint = Paint()
        paint.color = Color.RED

        // Draw a red rectangle covering the entire bitmap
        canvas.drawRect(0f, 0f, originalBitmap.width.toFloat(), originalBitmap.height.toFloat(), paint)

        // Return the result bitmap with the red layer
        return resultBitmap
    }


}
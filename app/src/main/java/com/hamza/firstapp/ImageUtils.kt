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

}
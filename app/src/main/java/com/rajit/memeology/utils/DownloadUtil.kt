package com.rajit.memeology.utils

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.load.resource.gif.GifDrawable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class DownloadUtil {

    companion object {

        fun downloadMeme(context: Context, fileName: String, drawable: Drawable, memeDownloadUrl: String) {

            var contentValues: ContentValues? = null

            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            when {
                fileName.contains(".gif") -> {
                    val file = File(context.getExternalFilesDir("Memeology"), fileName)
                    val request = DownloadManager.Request(Uri.parse(memeDownloadUrl))
                        .setMimeType("image/*")
                        .setDestinationUri(Uri.fromFile(file))
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                    val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)

                    Toast.makeText(context, "File saved to ${Uri.fromFile(file)}", Toast.LENGTH_LONG).show()
                }

                else -> {

                    val bitmapDrawable = drawable as BitmapDrawable
                    val bmp: Bitmap = bitmapDrawable.bitmap

                    contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                        put(MediaStore.Images.Media.WIDTH, bmp.width)
                        put(MediaStore.Images.Media.HEIGHT, bmp.height)
                    }

                    try {
                        context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                            context.contentResolver.openOutputStream(uri).use { outputStream ->
                                when {
                                    fileName.contains(".jpg") -> {
                                        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream!!)) {
                                            throw IOException("couldn't save bitmap")
                                        }
                                    }
                                    fileName.contains(".png") -> {
                                        if (!bmp.compress(Bitmap.CompressFormat.PNG, 95, outputStream!!)) {
                                            throw IOException("couldn't save bitmap")
                                        }
                                    }
                                }
                            }
                        } ?: throw IOException("Couldn't create Mediastore entry")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

}
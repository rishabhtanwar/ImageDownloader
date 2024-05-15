package com.example.imagedownloadertest.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.example.imagedownloadertest.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Collections.synchronizedMap
import java.util.Date
import java.util.Locale
import java.util.WeakHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ImageLoader(private val context: Context) {

    private val maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val memoryCache: LruCache<String, Bitmap>

    private val executorService: ExecutorService

    private val imageViewMap = synchronizedMap(WeakHashMap<ImageView, String>())
    private val handler: Handler
    private var diskLruImageCache: DiskLruImageCache

    init {
        memoryCache = object : LruCache<String, Bitmap>(maxCacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.byteCount / 1024
            }
        }
        diskLruImageCache = DiskLruImageCache(
            context,
            "image-downloader-test-new",
            Utils.DISK_CACHE_SIZE,
            Bitmap.CompressFormat.JPEG,
            70
        )
        executorService = Executors.newFixedThreadPool(5, Utils.ImageThreadFactory())
        handler = Handler()

        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels


    }


    companion object {

        private var INSTANCE: ImageLoader? = null

        internal var screenWidth = 0
        internal var screenHeight = 0

        @Synchronized
        fun with(context: Context): ImageLoader {

            require(context != null) {
                "ImageLoader:with - Context should not be null."
            }

            return INSTANCE ?: ImageLoader(context).also {
                INSTANCE = it
            }

        }
    }

    fun load(imageView: ImageView, imageUrl: String, uniqueId: String) {

        require(imageView != null) {
            "ImageLoader:load - ImageView should not be null."
        }

        require(imageUrl != null && imageUrl.isNotEmpty()) {
            "ImageLoader:load - Image Url should not be empty"
        }

        imageView.setImageResource(0)
        imageViewMap[imageView] = imageUrl

        val bitmap = checkImageInCache(imageUrl)
        bitmap?.let {
            loadImageIntoImageView(imageView, it, imageUrl)
        } ?: run {
            val diskBitmap = if (diskLruImageCache.containsKey(uniqueId)) diskLruImageCache.getBitmap(uniqueId) else null
            diskBitmap?.let {
                memoryCache.put(imageUrl, diskBitmap)
                val displayBitmap = DisplayBitmap(ImageRequest(imageUrl, imageView), diskBitmap)
                handler.post(displayBitmap)
            } ?: run {
                Log.e("TAG", "load: image from server")
                executorService.submit(PhotosLoader(ImageRequest(imageUrl, imageView), uniqueId))
            }
        }
    }

    @Synchronized
    private fun loadImageIntoImageView(imageView: ImageView, bitmap: Bitmap?, imageUrl: String) {

        require(bitmap != null) {
            "ImageLoader:loadImageIntoImageView - Bitmap should not be null"
        }

        val scaledBitmap = Utils.scaleBitmapForLoad(bitmap, imageView.width, imageView.height)

        scaledBitmap?.let {
            if (!isImageViewReused(ImageRequest(imageUrl, imageView))) imageView.setImageBitmap(
                scaledBitmap
            )
        }
    }

    private fun isImageViewReused(imageRequest: ImageRequest): Boolean {
        val tag = imageViewMap[imageRequest.imageView]
        return tag == null || tag != imageRequest.imgUrl
    }

    @Synchronized
    private fun checkImageInCache(imageUrl: String): Bitmap? = memoryCache.get(imageUrl)

    inner class DisplayBitmap(
        private var imageRequest: ImageRequest,
        private val originalBM: Bitmap?
    ) : Runnable {
        override fun run() {
            if (!isImageViewReused(imageRequest)) {
                val bitmap = checkImageInCache(imageRequest.imgUrl)
                loadImageIntoImageView(
                    imageRequest.imageView,
                    bitmap ?: originalBM,
                    imageRequest.imgUrl
                )
            }
        }
    }

    inner class ImageRequest(var imgUrl: String, var imageView: ImageView)

    inner class PhotosLoader(
        private var imageRequest: ImageRequest,
        private val uniqueName: String?
    ) : Runnable {

        override fun run() {

            if (isImageViewReused(imageRequest)) return

            val bitmap = Utils.downloadBitmapFromURL(imageRequest.imgUrl)
            if (bitmap==null){
                imageRequest.imageView.setImageResource(R.drawable.placeholder_image)
                return
            }
            memoryCache.put(imageRequest.imgUrl, bitmap)
            diskLruImageCache.put(uniqueName,bitmap)
            if (isImageViewReused(imageRequest)) return

            val displayBitmap = DisplayBitmap(imageRequest, bitmap)
            handler.post(displayBitmap)
        }
    }
}



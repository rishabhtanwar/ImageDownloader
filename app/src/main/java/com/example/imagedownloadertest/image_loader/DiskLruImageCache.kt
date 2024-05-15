package com.example.imagedownloadertest.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.example.imagedownloadertest.image_loader.DiskLruCache.Companion.open
import com.example.imagedownloadertest.util.Utils
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream

class DiskLruImageCache(
    context: Context, uniqueName: String?, diskCacheSize: Int,
    compressFormat: CompressFormat, quality: Int
) {
    private var mDiskCache: DiskLruCache? = null
    private var mCompressFormat = CompressFormat.JPEG
    private var mCompressQuality = 70

    init {
        try {
            val directory = context.getDir(uniqueName, Context.MODE_PRIVATE)
            mDiskCache = open(directory, APP_VERSION, VALUE_COUNT, diskCacheSize.toLong())
            mCompressFormat = compressFormat
            mCompressQuality = quality
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, FileNotFoundException::class)
    private fun writeBitmapToFile(bitmap: Bitmap, editor: DiskLruCache.Editor): Boolean {
        var out: OutputStream? = null
        try {
            out = BufferedOutputStream(editor.newOutputStream(0), Utils.IO_BUFFER_SIZE)
            return bitmap.compress(mCompressFormat, mCompressQuality, out)
        } finally {
            out?.close()
        }
    }


    fun put(key: String?, data: Bitmap) {
        var editor: DiskLruCache.Editor? = null
        try {
            editor = mDiskCache!!.edit(key!!)
            if (editor == null) {
                return
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskCache!!.flush()
                editor.commit()
                //                if ( BuildC.DEBUG ) {
//                    Log.d( "cache_test_DISK_", "image put on disk cache " + key );
//                }
            } else {
                editor.abort()
                //                if ( BuildConfig.DEBUG ) {
//                    Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
//                }
            }
        } catch (e: IOException) {
//            if ( BuildConfig.DEBUG ) {
//                Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
//            }
            try {
                editor?.abort()
            } catch (ignored: IOException) {
            }
        }
    }

    fun getBitmap(key: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            snapshot = mDiskCache!!.get(key!!)
            if (snapshot == null) {
                return null
            }
            val `in` = snapshot.getInputStream(0)
            if (`in` != null) {
                val buffIn =
                    BufferedInputStream(`in`, Utils.IO_BUFFER_SIZE)
                bitmap = BitmapFactory.decodeStream(buffIn)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            snapshot?.close()
        }

        //        if ( BuildConfig.DEBUG ) {
//            Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
//        }
        return bitmap
    }

    fun containsKey(key: String?): Boolean {
        var contained = false
        var snapshot: DiskLruCache.Snapshot? = null
        try {
            snapshot = mDiskCache!!.get(key!!)
            contained = snapshot != null
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            snapshot?.close()
        }

        return contained
    }

    fun clearCache() {
//        if ( BuildConfig.DEBUG ) {
//            Log.d( "cache_test_DISK_", "disk cache CLEARED");
//        }
        try {
            mDiskCache!!.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val cacheFolder: File
        get() = mDiskCache!!.directory

    companion object {
        private const val APP_VERSION = 1
        private const val VALUE_COUNT = 1
        private const val TAG = "DiskLruImageCache"
    }
}
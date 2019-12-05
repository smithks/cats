package com.keegansmith.cats.persistance

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.BreedModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class CatDownloadManager @Inject constructor(val catService: CatService,
                                             val filesDir: File,
                                             val moshi: Moshi) {

    suspend fun fileIsDownloaded(fileName: String): Boolean = withContext(Dispatchers.IO) {
            val file = File(filesDir, fileName)
            file.canRead()
        }

    suspend fun getFileSize(fileName: String): String = withContext(Dispatchers.IO) {
        val file = File(filesDir, fileName)
        if (file.exists() && file.isFile) {
            formatFileSize(file.length())
        } else {
            "${0}"
        }
    }

    private fun formatFileSize(long: Long): String {
        val inKbs = long / 1024.0
        return if (inKbs > 1024) {
            "${inKbs / 1024.0} mbs"
        } else {
            "$inKbs kbs"
        }
    }

    fun deleteFile(fileName: String): Boolean {
        val file = File(filesDir, fileName)
        return try {
            file.delete()
            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun fetchImageFromDisk(id: String): Bitmap = withContext(Dispatchers.IO) {
        try {
            val file = File(filesDir, id)
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            throw CacheLoadError("Error fetching from disk")
        }
    }

    suspend fun fetchDownloadedBreed(id: String): List<BreedModel> = withContext(Dispatchers.IO) {
        val inputStream = File(filesDir, id).inputStream()
        val responseBody = try {
            inputStream.readBytes().toString(Charsets.UTF_8)
        } catch (exception: IOException) {
            throw CacheLoadError("Error reading from cache")
        } finally {
            inputStream.close()
        }

        if (responseBody.isNotBlank()) {
            val adapter = moshi.adapter<List<BreedModel>>(
                Types.newParameterizedType(
                    List::class.java,
                    BreedModel::class.java
                )
            )
            adapter.fromJson(responseBody) ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun downloadImage(url:String, fileName: String): Bitmap {
        try {
            val responseBody = catService.downloadImage(url)
            writeToDisk(fileName, responseBody)
            return fetchImageFromDisk(fileName)
        } catch (ex: Throwable) {
            throw CacheLoadError("Error fetching from network")
        }
    }

    suspend fun downloadBreed(fileName: String): List<BreedModel> {
        try {
            val responseBody = catService.downloadBreeds()
            writeToDisk(fileName, responseBody)
            return fetchDownloadedBreed(fileName)
        } catch (ex: Throwable) {
            throw CacheLoadError("Error fetching from network")
        }
    }

    private suspend fun writeToDisk(id: String, responseBody: ResponseBody) = withContext(Dispatchers.IO) {
        try {
            File(filesDir, id).writeBytes(responseBody.bytes())
        } catch (exception: IOException) {
            throw CacheLoadError("Error saving to disk")
        }
    }
}

class CacheLoadError(message: String): Exception(message)

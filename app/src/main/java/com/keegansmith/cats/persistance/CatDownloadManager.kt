package com.keegansmith.cats.persistance

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.BreedModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class CatDownloadManager @Inject constructor(val catService: CatService,
                                             val filesDir: File,
                                             val moshi: Moshi) {

    fun fileIsDownloaded(fileName: String): Boolean {
        val file = File(filesDir, fileName)
        return file.canRead()
    }

    fun getFileSize(fileName: String): String {
        val file = File(filesDir, fileName)
        return if (file.exists() && file.isFile) {
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

    fun fetchImageFromDisk(id: String): Bitmap? {
        val file = File(filesDir, id)
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }
    }

    fun fetchDownloadedBreed(id: String): List<BreedModel> {
        val inputStream = File(filesDir, id).inputStream()
        val responseBody = try {
            inputStream.readBytes().toString(Charsets.UTF_8)
        } catch (exception: IOException) {
            ""
        } finally {
            inputStream.close()
        }

        return if (responseBody.isNotBlank()) {
            val adapter = moshi.adapter<List<BreedModel>>(Types.newParameterizedType(List::class.java, BreedModel::class.java))
            adapter.fromJson(responseBody) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun downloadImage(url:String, fileName: String, diskCallBack: DiskCallBack<Bitmap>) {
        catService.downloadImage(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                diskCallBack.onError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                if (body != null) {
                    if (writeToDisk(fileName, body)) {
                        val bitmap = fetchImageFromDisk(fileName)
                        if (bitmap != null) {
                            diskCallBack.onLoad(bitmap)
                        } else {
                            diskCallBack.onError()
                        }
                    } else {
                        diskCallBack.onError()
                    }
                }
            }
        })
    }

    fun downloadBreed(fileName: String, diskCallBack: DiskCallBack<List<BreedModel>>) {
        catService.downloadBreeds().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                diskCallBack.onError()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                // Store response body to disk, convert it and return it
                val body = response.body()
                if (body != null) {
                    if (writeToDisk(fileName, body)) {
                        diskCallBack.onLoad(fetchDownloadedBreed(fileName))
                    } else {
                        diskCallBack.onError()
                    }
                } else {
                    diskCallBack.onError()
                }
            }

        })
    }

    fun writeToDisk(id: String, responseBody: ResponseBody): Boolean =
        try {
            File(filesDir, id).writeBytes(responseBody.bytes())
            true
        } catch (exception: IOException) {
            false
        }
}

interface DiskCallBack<T> {
    fun onLoad(result: T)
    fun onError()
}
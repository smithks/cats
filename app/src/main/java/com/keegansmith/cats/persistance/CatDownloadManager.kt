package com.keegansmith.cats.persistance

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
import javax.inject.Inject

class CatDownloadManager @Inject constructor(val catService: CatService,
                                             val filesDir: File,
                                             val moshi: Moshi) {

    fun fetchDownloadedBreed(id: String): List<BreedModel> {
        val responseBody = try {
            File(filesDir, id).inputStream().readBytes().toString(Charsets.UTF_8)
        } catch (exception: IOException) {
            ""
        }

        if (responseBody.isNotBlank()) {
            val adapter = moshi.adapter<List<BreedModel>>(Types.newParameterizedType(List::class.java, BreedModel::class.java))
            return adapter.fromJson(responseBody) ?: emptyList()
        } else {
            return emptyList()
        }
    }

    fun downloadBreed(diskCallBack: DiskCallBack<List<BreedModel>>) {
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
                    if (writeToDisk("breeds", body)) {
                        diskCallBack.onLoad(fetchDownloadedBreed("breeds"))
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
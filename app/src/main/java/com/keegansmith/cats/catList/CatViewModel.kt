package com.keegansmith.cats.catList

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.BreedModel
import com.keegansmith.cats.api.model.CatModel
import com.keegansmith.cats.di.CatComponent
import com.keegansmith.cats.persistance.CatDownloadManager
import com.keegansmith.cats.persistance.DiskCallBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CatViewModel @Inject constructor(
    val catService: CatService,
    val catDownloadManager: CatDownloadManager
): ViewModel() {

    private val textFileName = "breeds"
    private val imageFileName = "catPic"

    fun init(component: CatComponent) {
        cacheTextFileSize.postValue("File Size: Unknown")
        cacheImageFileSize.postValue("File Size: Unknown")
    }

    val catList: MutableLiveData<List<CatModel>> = MutableLiveData()
    val errorMessage: MutableLiveData<Unit> = MutableLiveData()
    val cacheText: MutableLiveData<List<BreedModel>> = MutableLiveData()
    val cacheTextFileSize: MutableLiveData<String> = MutableLiveData()
    val cacheImage: MutableLiveData<Bitmap?> = MutableLiveData()
    val cacheImageFileSize: MutableLiveData<String> = MutableLiveData()

    fun fetchCatList() {
        catList.value = emptyList()
        catService.fetchRandomCats().enqueue(object : Callback<List<CatModel>> {
            override fun onFailure(call: Call<List<CatModel>>, t: Throwable) {
                errorMessage.postValue(Unit)
            }

            override fun onResponse(
                call: Call<List<CatModel>>,
                response: Response<List<CatModel>>
            ) {
                response.body()?.let {
                    catList.value = it
                }
            }

        })
    }

    fun fetchSingleImage() {
        if (catDownloadManager.fileIsDownloaded(imageFileName)) {
            cacheImage.postValue(catDownloadManager.fetchImageFromDisk(imageFileName))
            updateImageFileSize()
        } else {
            catDownloadManager.downloadImage(
                "https://cdn2.thecatapi.com/images/251.jpg",
                imageFileName,
                object : DiskCallBack<Bitmap> {
                    override fun onLoad(result: Bitmap) {
                        cacheImage.postValue(result)
                        updateImageFileSize()
                    }

                    override fun onError() {
                        cacheImage.postValue(null)
                    }

                })
        }
    }

    fun deleteImage() {
        catDownloadManager.deleteFile(imageFileName)
        cacheImage.postValue(null)
        updateImageFileSize()
    }

    fun updateImageFileSize() {
        cacheImageFileSize.postValue("File Size: ${catDownloadManager.getFileSize(imageFileName)}")
    }

    fun fetchText() {

        cacheText.value = emptyList()

        // check the download manager to see if we have downloaded this file already
        if (catDownloadManager.fileIsDownloaded(textFileName)) {
            cacheText.postValue(catDownloadManager.fetchDownloadedBreed(textFileName))
            updateTextFileSize()
        } else {
            // Otherwise download and post it
            catDownloadManager.downloadBreed(textFileName, object : DiskCallBack<List<BreedModel>> {
                override fun onLoad(result: List<BreedModel>) {
                    cacheText.postValue(result)
                    updateTextFileSize()
                }

                override fun onError() {
                    errorMessage.postValue(Unit)
                }
            })
        }
    }

    fun deleteText() {
        val deleted = catDownloadManager.deleteFile(textFileName)
        if (deleted) {
            cacheText.postValue(emptyList())
            updateTextFileSize()
        }
    }

    fun updateTextFileSize() {
        cacheTextFileSize.postValue("File Size: ${catDownloadManager.getFileSize(textFileName)}")
    }
}
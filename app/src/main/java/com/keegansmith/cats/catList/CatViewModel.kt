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

class CatViewModel : ViewModel() {

    @Inject
    lateinit var catService: CatService

    @Inject
    lateinit var catDownloadManager: CatDownloadManager

    private val breedsFileName = "breeds"
    private val imageFileName = "catPic"

    fun init(component: CatComponent) {
        component.inject(this)
        breedFileSize.postValue("File Size: Unknown")
    }

    val catList: MutableLiveData<List<CatModel>> = MutableLiveData()
    val errorMessage: MutableLiveData<Unit> = MutableLiveData()
    val breedList: MutableLiveData<List<BreedModel>> = MutableLiveData()
    val breedFileSize: MutableLiveData<String> = MutableLiveData()
    val singleCatImage: MutableLiveData<Bitmap?> = MutableLiveData()
    val imageFileSize: MutableLiveData<String> = MutableLiveData()

    fun fetchCats() {
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

    fun deleteBreeds() {
        val deleted = catDownloadManager.deleteFile(breedsFileName)
        if (deleted) {
            breedList.postValue(emptyList())
            updateBreedFileSize()
        }
    }

    fun fetchSingleImage() {
        if (catDownloadManager.fileIsDownloaded(imageFileName)) {
            singleCatImage.postValue(catDownloadManager.fetchImageFromDisk(imageFileName))
            updateImageFileSize()
        } else {
            catDownloadManager.downloadImage(
                "https://cdn2.thecatapi.com/images/251.jpg",
                imageFileName,
                object : DiskCallBack<Bitmap> {
                    override fun onLoad(result: Bitmap) {
                        singleCatImage.postValue(result)
                        updateImageFileSize()
                    }

                    override fun onError() {
                        singleCatImage.postValue(null)
                    }

                })
        }
    }

    fun deleteImage() {
        catDownloadManager.deleteFile(imageFileName)
        singleCatImage.postValue(null)
        updateImageFileSize()
    }

    fun fetchBreeds() {
        breedList.value = emptyList()

        // check the download manager to see if we have downloaded this file already
        if (catDownloadManager.fileIsDownloaded(breedsFileName)) {
            breedList.postValue(catDownloadManager.fetchDownloadedBreed(breedsFileName))
            updateBreedFileSize()
        } else {
            // Otherwise download and post it
            catDownloadManager.downloadBreed(breedsFileName, object : DiskCallBack<List<BreedModel>> {
                override fun onLoad(result: List<BreedModel>) {
                    breedList.postValue(result)
                    updateBreedFileSize()
                }

                override fun onError() {
                    errorMessage.postValue(Unit)
                }
            })
        }
    }

    fun updateImageFileSize() {
        imageFileSize.postValue("File Size: ${catDownloadManager.getFileSize(imageFileName)}")
    }

    fun updateBreedFileSize() {
        breedFileSize.postValue("File Size: ${catDownloadManager.getFileSize(breedsFileName)}")
    }
}
package com.keegansmith.cats.catList

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.BreedModel
import com.keegansmith.cats.api.model.CatModel
import com.keegansmith.cats.di.CatComponent
import com.keegansmith.cats.persistance.CacheLoadError
import com.keegansmith.cats.persistance.CatDownloadManager
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            try {
                catList.value = emptyList()
                val cats = catService.fetchRandomCats()
                catList.value = cats
            } catch (ex: Throwable) {
                errorMessage.postValue(Unit)
            }
        }
    }

    fun fetchSingleImage() {
        viewModelScope.launch {
            try {
                if (catDownloadManager.fileIsDownloaded(imageFileName)) {
                    cacheImage.value = catDownloadManager.fetchImageFromDisk(imageFileName)
                } else {
                    cacheImage.value = catDownloadManager.downloadImage(
                        "https://cdn2.thecatapi.com/images/251.jpg",
                        imageFileName
                    )
                }
                updateImageFileSize()
            } catch (ex: CacheLoadError) {
                cacheImage.value = null
            }
        }
    }

    fun deleteImage() {
        catDownloadManager.deleteFile(imageFileName)
        cacheImage.postValue(null)
        updateImageFileSize()
    }

    private fun updateImageFileSize() {
        viewModelScope.launch {
            cacheImageFileSize.postValue("File Size: ${catDownloadManager.getFileSize(imageFileName)}")
        }
    }

    fun fetchText() {
        viewModelScope.launch {
            try {
                // check the download manager to see if we have downloaded this file already
                if (catDownloadManager.fileIsDownloaded(textFileName)) {
                    cacheText.value = catDownloadManager.fetchDownloadedBreed(textFileName)
                } else {
                    cacheText.value = catDownloadManager.downloadBreed(textFileName)
                }
                updateTextFileSize()
            } catch (ex: CacheLoadError) {
                cacheText.value = emptyList()
            }
        }
    }

    fun deleteText() {
        val deleted = catDownloadManager.deleteFile(textFileName)
        if (deleted) {
            cacheText.postValue(emptyList())
            updateTextFileSize()
        }
    }

    private fun updateTextFileSize() {
        viewModelScope.launch {
            cacheTextFileSize.postValue("File Size: ${catDownloadManager.getFileSize(textFileName)}")
        }
    }
}
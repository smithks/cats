package com.keegansmith.cats.catList

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

    fun init(component: CatComponent) {
        component.inject(this)
    }

    val catList: MutableLiveData<List<CatModel>> = MutableLiveData()
    val errorMessage: MutableLiveData<Unit> = MutableLiveData()
    val breedList: MutableLiveData<List<BreedModel>> = MutableLiveData()

    fun fetchCats() {
        catList.value = emptyList()
        catService.fetchRandomCats()?.enqueue(object : Callback<List<CatModel>> {
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

    fun fetchBreeds() {
        breedList.value = emptyList()

        // check the download manager to see if we have downloaded this file already and if we should download it

        catDownloadManager.downloadBreed(object : DiskCallBack<List<BreedModel>> {
            override fun onLoad(result: List<BreedModel>) {
                breedList.postValue(result)
            }

            override fun onError() {
                errorMessage.postValue(Unit)
            }
        })

        // If we do not have the file then we should fetch it for normal display
//        catService.fetchBreeds().enqueue(object : Callback<List<BreedModel>> {
//            override fun onFailure(call: Call<List<BreedModel>>, t: Throwable) {
//                errorMessage.postValue(Unit)
//            }
//
//            override fun onResponse(
//                call: Call<List<BreedModel>>,
//                response: Response<List<BreedModel>>
//            ) {
//                response.body()?.let {
//                    breedList.postValue(it)
//                }
//            }
//        })
    }
}
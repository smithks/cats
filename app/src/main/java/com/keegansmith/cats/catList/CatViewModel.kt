package com.keegansmith.cats.catList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.BreedModel
import com.keegansmith.cats.api.model.CatModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatViewModel: ViewModel() {

    var catService: CatService? = null

    val catList: MutableLiveData<List<CatModel>> = MutableLiveData()
    val errorMessage: MutableLiveData<Unit> = MutableLiveData()
    val breedList: MutableLiveData<List<BreedModel>> = MutableLiveData()

    fun fetchCats() {
        catList.value = emptyList()
        catService?.fetchRandomCats()?.enqueue(object: Callback<List<CatModel>> {
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
        catService?.fetchBreeds()?.enqueue(object : Callback<List<BreedModel>> {
            override fun onFailure(call: Call<List<BreedModel>>, t: Throwable) {
                errorMessage.postValue(Unit)
            }

            override fun onResponse(
                call: Call<List<BreedModel>>,
                response: Response<List<BreedModel>>
            ) {
                response.body()?.let {
                    breedList.postValue(it)
                }
            }
        })
    }
}
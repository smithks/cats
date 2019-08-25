package com.keegansmith.cats.catList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.CatModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatViewModel: ViewModel() {

    var catService: CatService? = null

    val catList: MutableLiveData<List<CatModel>> = MutableLiveData()

    fun fetchCats() {
        catService?.fetchRandomCats()?.enqueue(object: Callback<List<CatModel>> {
            override fun onFailure(call: Call<List<CatModel>>, t: Throwable) {
                //TODO error message
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
}
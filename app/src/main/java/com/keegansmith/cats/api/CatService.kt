package com.keegansmith.cats.api


import com.keegansmith.cats.api.model.BreedModel
import com.keegansmith.cats.api.model.CatModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface CatService {

    @GET("v1/images/search?limit=10&mime_types=jpg,png")
    fun fetchRandomCats(): Call<List<CatModel>>

    @GET("v1/breeds?limit=10")
    fun fetchBreeds(): Call<List<BreedModel>>

    @GET("v1/breeds?limit=10")
    fun downloadBreeds(): Call<ResponseBody>
}
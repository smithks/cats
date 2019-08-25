package com.keegansmith.cats

import android.app.Application
import com.keegansmith.cats.api.CatService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CatApplication: Application() {

    lateinit var catService: CatService

    override fun onCreate() {
        super.onCreate()

        // TODO inject service
        catService = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CatService::class.java)

    }
}
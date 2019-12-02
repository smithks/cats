package com.keegansmith.cats

import android.app.Application
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.di.CatComponent
import com.keegansmith.cats.di.CatModule
import com.keegansmith.cats.di.DaggerCatComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CatApplication: Application() {

    lateinit var catComponent: CatComponent

    override fun onCreate() {
        super.onCreate()

        catComponent = DaggerCatComponent.builder()
            .catModule(CatModule(this))
            .build()
    }

}
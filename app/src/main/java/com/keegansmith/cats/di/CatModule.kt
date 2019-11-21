package com.keegansmith.cats.di

import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.api.CatService
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class CatModule(var application: CatApplication) {

    @Provides
    @Singleton
    fun getCatService(okHttpClient: OkHttpClient): CatService {
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CatService::class.java)
    }

    @Provides
    fun getOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .build()
    }

    @Provides
    fun getCache(): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        return Cache(application.cacheDir, cacheSize)
    }
}
package com.keegansmith.cats.di

import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.api.CatService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton


@Module
class CatModule(var application: CatApplication) {

    @Provides
    @Singleton
    fun getCatService(okHttpClient: OkHttpClient): CatService {
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(MoshiConverterFactory.create())
//            .client(okHttpClient)
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
    fun getMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    fun getFilesDir(): File {
        return application.filesDir
    }

    @Provides
    fun getCache(): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        // We can specify whatever cache location we want to use here but it will always be treated
        // as a cache: files created and deleted as needed to comply with the given cache size.
        // We cannot guarantee a file will remain on disk once it is downloaded.
        return Cache(application.cacheDir, cacheSize)
    }
}
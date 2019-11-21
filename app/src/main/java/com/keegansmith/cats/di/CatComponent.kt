package com.keegansmith.cats.di

import com.keegansmith.cats.api.CatService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CatModule::class])
interface CatComponent {

    fun catService(): CatService

}
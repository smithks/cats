package com.keegansmith.cats.di

import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.catList.CatViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CatModule::class])
interface CatComponent {

    fun inject(catViewModel: CatViewModel)

}
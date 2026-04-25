package com.example.myfitness.di

import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.presentation.activity.AuthActivity
import com.example.myfitness.presentation.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: AuthActivity)
    fun foodRepository(): FoodRepository
    fun apiService(): ApiService
}
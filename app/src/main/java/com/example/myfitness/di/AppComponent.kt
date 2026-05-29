package com.example.myfitness.di

import com.example.myfitness.presentation.activity.AuthActivity
import com.example.myfitness.presentation.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: AuthActivity)
    fun viewModelFactory(): ViewModelFactory
}

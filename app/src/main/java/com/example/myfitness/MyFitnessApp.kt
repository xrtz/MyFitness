package com.example.myfitness

import android.app.Application
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.di.AppComponent
import com.example.myfitness.di.AppModule
import com.example.myfitness.di.DaggerAppComponent

class MyFitnessApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        TokenProvider.init(this)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}
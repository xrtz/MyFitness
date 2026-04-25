package com.example.myfitness.di

import android.content.Context
import androidx.room.Room
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.AuthInterceptor
import com.example.myfitness.data.repository.FoodRepositoryImpl
import com.example.myfitness.data.storage.Storage
import com.example.myfitness.data.storage.room.AppDatabase
import com.example.myfitness.data.storage.room.RoomStorageImpl
import com.example.myfitness.data.storage.room.dao.FoodDao
import com.example.myfitness.data.storage.room.dao.UserDao
import com.example.myfitness.domain.repository.FoodRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {


    @Provides @Singleton
    fun provideAppDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "myfitness.db")
            .allowMainThreadQueries()
            .build()

    @Provides @Singleton fun provideFoodDao(db: AppDatabase): FoodDao = db.foodDao()
    @Provides @Singleton fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides @Singleton fun provideStorage(impl: RoomStorageImpl): Storage = impl
    @Provides @Singleton fun provideFoodRepository(impl: FoodRepositoryImpl): FoodRepository = impl


    @Provides @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.31.187:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
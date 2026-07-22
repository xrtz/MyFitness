package com.example.myfitness.di

import android.content.Context
import androidx.room.Room
import com.example.myfitness.data.datasource.remote.ApiRemoteFoodDataSource
import com.example.myfitness.data.datasource.remote.ApiRemoteUserDataSource
import com.example.myfitness.data.datasource.remote.RemoteFoodDataSource
import com.example.myfitness.data.datasource.remote.RemoteUserDataSource
import com.example.myfitness.data.remote.ApiService
import com.example.myfitness.data.remote.AuthInterceptor
import com.example.myfitness.data.repository.FoodRepositoryImpl
import com.example.myfitness.data.repository.UserRepositoryImpl
import com.example.myfitness.data.storage.Storage
import com.example.myfitness.data.storage.room.AppDatabase
import com.example.myfitness.data.storage.room.RoomStorageImpl
import com.example.myfitness.data.storage.room.dao.FoodDao
import com.example.myfitness.data.storage.room.dao.UserDao
import com.example.myfitness.domain.repository.FoodRepository
import com.example.myfitness.domain.repository.UserRepository
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

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "myfitness.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideFoodDao(db: AppDatabase): FoodDao = db.foodDao()
    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideStorage(impl: RoomStorageImpl): Storage = impl
    @Provides
    @Singleton
    fun provideRemoteFoodDataSource(impl: ApiRemoteFoodDataSource): RemoteFoodDataSource = impl
    @Provides
    @Singleton
    fun provideRemoteUserDataSource(impl: ApiRemoteUserDataSource): RemoteUserDataSource = impl
    @Provides
    @Singleton
    fun provideFoodRepository(impl: FoodRepositoryImpl): FoodRepository = impl
    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}

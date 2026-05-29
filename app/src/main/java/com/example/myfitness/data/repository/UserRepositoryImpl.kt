package com.example.myfitness.data.repository

import com.example.myfitness.data.datasource.remote.RemoteUserDataSource
import com.example.myfitness.data.remote.TokenProvider
import com.example.myfitness.data.storage.room.dao.UserDao
import com.example.myfitness.data.storage.room.entity.UserEntity
import com.example.myfitness.domain.models.UserModel
import com.example.myfitness.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource : RemoteUserDataSource,
    private val userDao          : UserDao
) : UserRepository {

    override suspend fun login(email: String, password: String): String {
        val (token, userId) = remoteDataSource.login(email, password)
        TokenProvider.save(token, userId)
        return token
    }

    override suspend fun register(
        name     : String,
        email    : String,
        password : String,
        gender   : Int,
        weight   : Float,
        height   : Float,
        target   : String
    ): String {
        val (token, userId) = remoteDataSource.register(name, email, password, gender, weight, height, target)
        TokenProvider.save(token, userId)
        return token
    }

    override suspend fun getProfile(): UserModel {
        val user     = remoteDataSource.getProfile()
        val existing = userDao.getUser()
        if (existing == null) userDao.insertUser(user.toEntity())
        else userDao.updateUser(user.toEntity().copy(id = existing.id))
        return user
    }

    override suspend fun updateProfile(user: UserModel): UserModel {
        val updated  = remoteDataSource.updateProfile(user)
        val existing = userDao.getUser()
        if (existing == null) userDao.insertUser(updated.toEntity())
        else userDao.updateUser(updated.toEntity().copy(id = existing.id))
        return updated
    }

    private fun UserModel.toEntity() = UserEntity(
        id     = id,
        name   = name,
        gender = gender,
        gmail  = gmail,
        weight = weight,
        height = height,
        target = target
    )
}

package com.example.myfitness.data.remote


object TokenProvider {
    @Volatile
    var token: String? = null
}
package com.example.myfitness.data.remote

import android.content.Context
import android.content.SharedPreferences

object TokenProvider {
    @Volatile var token: String? = null

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        token = prefs?.getString("jwt_token", null)
    }

    fun save(newToken: String) {
        token = newToken
        prefs?.edit()?.putString("jwt_token", newToken)?.apply()
    }

    fun clear() {
        token = null
        prefs?.edit()?.remove("jwt_token")?.apply()
    }
}

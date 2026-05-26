package com.example.myfitness.data.remote

import android.content.Context
import android.content.SharedPreferences

object TokenProvider {
    @Volatile var token: String? = null
    @Volatile var userId: String? = null

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        token  = prefs?.getString("jwt_token", null)
        userId = prefs?.getString("user_id", null)
    }

    fun save(newToken: String, newUserId: String) {
        token  = newToken
        userId = newUserId
        prefs?.edit()
            ?.putString("jwt_token", newToken)
            ?.putString("user_id", newUserId)
            ?.apply()
    }

    fun clear() {
        token  = null
        userId = null
        prefs?.edit()
            ?.remove("jwt_token")
            ?.remove("user_id")
            ?.apply()
    }
}

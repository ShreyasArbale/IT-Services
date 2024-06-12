package com.example.maintenance.storage

import android.content.Context
import android.util.Log
import com.example.maintenance.login.model_login.KeyDetails

class SharedPreferenceManager private constructor(
    private val mCtx: Context
) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("user", false)
        }

    val getToken: String
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("token", "").toString()
        }

    fun saveLoginStatus() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("user", true)
        editor.apply()
    }

    fun saveBaseUrl(baseUrl: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("baseUrl", baseUrl)
        editor.apply()
    }

    fun saveUserName(User: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("User", User)
        editor.apply()
    }

    val getBaseUrl: String
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("baseUrl", "").toString()
        }

    val getUser: String
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("User", "").toString()
        }

    fun saveUser(user: KeyDetails) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val token = "token ${user.api_key}:${user.api_secret}"
        editor.putString("token", token)
        editor.apply()
        Log.d("editor", "User saved with token: $token")
    }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("user")
        editor.remove("token")
        editor.remove("baseUrl")
        editor.apply()
        Log.d("editor", "Cleared shared preferences")
    }

    companion object {
        private const val SHARED_PREF_NAME = "my_shared_preff"
        private var mInstance: SharedPreferenceManager? = null

        @Synchronized
        fun getInstance(mCtx: Context): SharedPreferenceManager {
            if (mInstance == null) {
                mInstance = SharedPreferenceManager(mCtx)
            }
            return mInstance as SharedPreferenceManager
        }
    }
}

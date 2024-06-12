package com.example.maintenance.issue_type

import com.example.maintenance.ApiInterface
import com.example.maintenance.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IssueTypeRetrofitInstance {

    private var retrofit: Retrofit? = null
    private var baseUrl: String = Utils.BASE

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofit = null
    }

    val api: ApiInterface by lazy {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        retrofit!!.create(ApiInterface::class.java)
    }
}
package com.example.maintenance.calendar

import com.example.maintenance.ApiInterface
import com.example.maintenance.Utils
import com.example.maintenance.issue.IssueApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ScheduledDateRetrofitInstance {
//    val api: ApiInterface by lazy {
//        Retrofit.Builder()
//            .baseUrl(Utils.BASE)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiInterface::class.java)
//    }

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
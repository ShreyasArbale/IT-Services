package com.example.maintenance.issue

import com.example.maintenance.Utils
import com.example.maintenance.visit_maintenance.VisitApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IssueRetrofitInstance {
//    val api: IssueApiInterface by lazy {
//        Retrofit.Builder()
//            .baseUrl(Utils.BASE)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(IssueApiInterface::class.java)
//    }

    private var retrofit: Retrofit? = null
    private var baseUrl: String = Utils.BASE

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofit = null
    }

    val api: IssueApiInterface by lazy {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        retrofit!!.create(IssueApiInterface::class.java)
    }
}
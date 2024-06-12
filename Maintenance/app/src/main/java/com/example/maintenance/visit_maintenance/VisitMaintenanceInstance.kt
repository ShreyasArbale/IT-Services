package com.example.maintenance.visit_maintenance

import com.example.maintenance.ApiInterface
import com.example.maintenance.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VisitMaintenanceInstance {
//    val api: VisitApiInterface by lazy {
//        Retrofit.Builder()
//            .baseUrl(Utils.BASE)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(VisitApiInterface::class.java)
//    }

    private var retrofit: Retrofit? = null
    private var baseUrl: String = Utils.BASE

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofit = null
    }

    val api: VisitApiInterface by lazy {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        retrofit!!.create(VisitApiInterface::class.java)
    }
}
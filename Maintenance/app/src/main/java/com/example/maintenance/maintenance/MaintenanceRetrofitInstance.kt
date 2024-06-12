package com.example.maintenance.maintenance

import com.example.maintenance.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object MaintenanceRetrofitInstance {
//    val api: MaintenanceApiInterface by lazy {
//        Retrofit.Builder()
//            .baseUrl(Utils.BASE)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(MaintenanceApiInterface::class.java)
//    }

    private var retrofit: Retrofit? = null
    private var baseUrl: String = Utils.BASE

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofit = null
    }

    val api: MaintenanceApiInterface by lazy {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        retrofit!!.create(MaintenanceApiInterface::class.java)
    }
}
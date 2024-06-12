package com.example.maintenance.maintenance.home_maintenance_activity

import com.example.maintenance.ApiInterface
import com.example.maintenance.Utils
import com.example.maintenance.maintenance.MaintenanceApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object HomeMaintenanceRetrofitInstance {
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
        retrofit = null // Reset retrofit instance when base URL changes
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
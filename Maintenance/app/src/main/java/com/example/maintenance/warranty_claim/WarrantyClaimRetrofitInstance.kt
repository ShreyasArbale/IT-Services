package com.example.maintenance.warranty_claim

import com.example.maintenance.Utils
import com.example.maintenance.visit_maintenance.VisitApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WarrantyClaimRetrofitInstance {
//    val api: WarrantyApiInterface by lazy {
//        Retrofit.Builder()
//            .baseUrl(Utils.BASE)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(WarrantyApiInterface::class.java)
//    }

    private var retrofit: Retrofit? = null
    private var baseUrl: String = Utils.BASE

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofit = null
    }

    val api: WarrantyApiInterface by lazy {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        retrofit!!.create(WarrantyApiInterface::class.java)
    }
}
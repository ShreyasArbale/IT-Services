package com.example.maintenance.resoledBy

import com.example.maintenance.ApiInterface
import com.example.maintenance.Utils
import com.example.maintenance.warranty_claim.WarrantyApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ResoledByRetrofitInstance {
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

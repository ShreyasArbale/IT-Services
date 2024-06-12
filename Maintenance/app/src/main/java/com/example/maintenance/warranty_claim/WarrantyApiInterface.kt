package com.example.maintenance.warranty_claim

import com.example.maintenance.warranty_claim.add_warranty_claim.add_warranty_claim_model.AddWarrantyClaim
import com.example.maintenance.warranty_claim.edit_warranty_claim.model_edit_warranty.EditWarrantyClaim
import com.example.maintenance.warranty_claim.model_warranty_claim.Warranty
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WarrantyApiInterface {

    @GET("/api/resource/Warranty Claim?fields=[\"name\",\"customer\",\"item_code\",\"complaint_date\",\"status\"]")
    suspend fun getWarranty(@Header("Authorization") token: String): Response<Warranty>

    @FormUrlEncoded
    @POST("/api/resource/Warranty Claim")
    suspend fun createWarrantyClaim(
        @Header("Authorization") token: String,

        @Field("name") name: String,
        @Field("customer") customer: String,
        @Field("complaint_date") complaint_date: String,
        @Field("complaint") complaint: String,
        @Field("item_code") item_code: String,
        @Field("warranty_amc_status") warranty_amc_status: String,
        @Field("warranty_expiry_date") warranty_expiry_date: String,
        @Field("amc_expiry_date") amc_expiry_date: String,
        @Field("resolution_date") resolution_date: String,
        @Field("resolved_by") resolved_by: String,
        @Field("resolution_details") resolution_details: String

        ): Response<AddWarrantyClaim>

//    Edit Data
    @GET("/api/resource/Warranty Claim/{id}")
    fun getEditWarranty(@Header("Authorization") token: String,
                     @Path("id") id: String
    ): Call<EditWarrantyClaim>

    @FormUrlEncoded
    @PUT("/api/resource/Warranty Claim/{id}")
    suspend fun editWarrantyClaim(
        @Header("Authorization") token: String,
        @Path("id") id: String,
//        @Field("name") name: String,
        @Field("customer") customer: String,
        @Field("complaint_date") complaint_date: String,
        @Field("complaint") complaint: String,
        @Field("item_code") item_code: String,
        @Field("warranty_amc_status") warranty_amc_status: String,
        @Field("warranty_expiry_date") warranty_expiry_date: String,
        @Field("amc_expiry_date") amc_expiry_date: String,
        @Field("resolution_date") resolution_date: String,
        @Field("resolved_by") resolved_by: String,
        @Field("resolution_details") resolution_details: String

        ): Response<EditWarrantyClaim>

    @DELETE("/api/resource/Warranty Claim/{id}")
    suspend fun deleteWarrantyClaim(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<EditWarrantyClaim>

}
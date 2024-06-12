package com.example.maintenance.maintenance

import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.AddMaintenanceRequestModel
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.EditMaintenance
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.Item
import com.example.maintenance.maintenance.home_maintenance_activity.model_home_maintenance.MaintenanceData
import com.example.maintenance.warranty_claim.edit_warranty_claim.model_edit_warranty.EditWarrantyClaim
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MaintenanceApiInterface {

    @GET("/api/resource/Maintenance Schedule?fields=[\"name\",\"customer\",\"transaction_date\",\"status\"]")
    suspend fun getMaintenance(@Header("Authorization") token: String): Response<MaintenanceData>

    //    To add maintenance
    @POST("/api/resource/Maintenance Schedule")
    suspend fun addMaintenance(
        @Header("Authorization") token: String,
        @Body maintenanceRequestModel: AddMaintenanceRequestModel
//        @Field("name") name: String,
//        @Field("customer_name") customer_name: String,
//        @Field("transaction_date") transaction_date: String,
//        @Field("status") status: String,
//        @Field("item_code") item_code : String,
//        @Field("item_name") item_name : String,
//        @Field("start_date") start_date : String,
//        @Field("end_date") end_date : String,
//        @Field("periodicity") periodicity : String,
//        @Field("no_of_visits") no_of_visits : String,
//        @Field("sales_person") sales_person : String,
//        @Field("items") items:Item

    ): Response<AddMaintenanceRequestModel>

    @GET("/api/resource/Maintenance Schedule/{id}")
    fun getEditMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<EditMaintenance>

//    to get items
    @GET("/api/resource/Maintenance Schedule/{id}")
    suspend fun getItemMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<EditMaintenance>

//    to get schedules
    @GET("/api/resource/Maintenance Schedule/{id}")
    suspend fun getScheduleMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<EditMaintenance>


    @PUT("/api/resource/Maintenance Schedule/{id}")
    suspend fun editMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String,
//        @Field("customer_name") customer: String,
//        @Field("transaction_date") transaction_date: String,
        @Body editMaintenanceRequestModel: AddMaintenanceRequestModel
        ): Response<EditMaintenance>

    @DELETE("/api/resource/Maintenance Schedule/{id}")
    suspend fun deleteMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<EditMaintenance>

    @DELETE("/api/resource/Maintenance Schedule/{id}")
    suspend fun deleteMaintenanceItem(
        @Header("Authorization") token: String,
        @Path("id") id: String,
//        @Path("idx") idx: String
    ): Response<EditMaintenance>

}

    //    To get data in edit maintenance activity
//    @GET("https://mobilecrm.erpdata.in/api/resource/Maintenance Schedule/{id}")
//    fun getEditMaintenance(
//        @Header("Authorization") token: String,
//        @Path("id") id: String
//    ): Call<Main>

//    @GET("https://mobilecrm.erpdata.in/api/resource/Maintenance Schedule/{id}")
//    fun getItemShowMaintenance(
//        @Header("Authorization") token: String,
//        @Path("id") id: String
//    ): Call<ItemDataItem>
//
//    //    To edit maintenance
//    @FormUrlEncoded
//    @PUT("https://mobilecrm.erpdata.in/api/resource/Maintenance Schedule/{id}")
//    suspend fun editMaintenance(
//        @Header("Authorization") token: String,
//        @Path("id") id: String,
//        @Field("name") name: String,
//        @Field("customer_name") customer_name: String,
//        @Field("status") status: String,
//        @Field("transaction_date") transaction_date: String,
//
//        ): Response<Main>


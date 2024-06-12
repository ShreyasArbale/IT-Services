package com.example.maintenance.visit_maintenance

import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.AddMaintenanceRequestModel
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.EditMaintenance
import com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.AddVisitMaintenanceModel
import com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_edit_visit_maintenance.EditVisitMaintenance
import com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_old_edit_visit_maintenance.OldVisitMaintenance
import com.example.maintenance.visit_maintenance.model_visit.Visit
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VisitApiInterface {

    @GET("/api/resource/Maintenance Visit?fields=[\"name\",\"customer\",\"completion_status\",\"maintenance_type\",\"status\"]")
    suspend fun getVisitMaintenance(@Header("Authorization") token: String): Response<Visit>

    @POST("/api/resource/Maintenance Visit")
    suspend fun addVisitMaintenance(
        @Header("Authorization") token: String,
        @Body maintenanceVisitRequestModel: AddVisitMaintenanceModel
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

    ): Response<AddVisitMaintenanceModel>

    @GET("/api/resource/Maintenance Visit/{id}?fields=[\"customer\"]")
    fun getEditVisitMaintenance(@Header("Authorization") token: String,
                           @Path("id") id: String
    ): Call<EditVisitMaintenance>

    @GET("/api/resource/Maintenance Visit/{id}")
    suspend fun getItemVisitMaintenance(@Header("Authorization") token: String,
                                   @Path("id") id: String
    ): Response<EditVisitMaintenance>

    @PUT("/api/resource/Maintenance Visit/{id}")
    suspend fun editVisitMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body editVisitMaintenanceRequestModel: AddVisitMaintenanceModel
    ): Response<EditVisitMaintenance>

    @DELETE("/api/resource/Maintenance Visit/{id}")
    suspend fun deleteVisitMaintenance(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<EditVisitMaintenance>


    @GET("/api/resource/Maintenance Visit/{id}")
    suspend fun getOldItemVisitMaintenance(@Header("Authorization") token: String,
                                        @Path("id") id: String
    ): Response<OldVisitMaintenance>

}
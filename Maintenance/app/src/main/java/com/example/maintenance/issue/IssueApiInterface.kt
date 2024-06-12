package com.example.maintenance.issue

import com.example.maintenance.issue.add_issue.model_add_issue.IssueItem
import com.example.maintenance.issue.edit_issue.model_edit_issue.DeleteIssueModel
import com.example.maintenance.issue.edit_issue.model_edit_issue.Edit
import com.example.maintenance.issue.edit_issue.model_edit_issue.EditIssueItem
import com.example.maintenance.issue.model_issue.Issue
import com.example.maintenance.issue.model_item_name.ItemNameItem
import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.AddMaintenanceRequestModel
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.EditMaintenance
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IssueApiInterface {
    @GET("/api/resource/Issue?fields=[\"name\",\"subject\",\"raised_by\",\"status\",\"priority\"]&order_by=creation desc")
    suspend fun getIssue(@Header("Authorization") token: String): Response<Issue>

    //https://mobilecrm.erpdata.in/api/resource/Issue

    //    @FormUrlEncoded
    @POST("/api/resource/Issue")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Body IssueRequestModel: IssueItem

//        @Field("name") name: String,
//        @Field("subject") subject: String,
//        @Field("customer") customer: String,
//        @Field("custom_department") custom_department: String,
//        @Field("custom_mob_number") custom_mob_number: String,
//        @Field("custom_location") custom_location: String,
//        @Field("custom_office") custom_office: String,
//        @Field("custom_contact_person") custom_contact_person: String,
//        @Field("custom_assign_user") custom_assign_user: String,
//        @Field("action_taken") action_taken: String,
//        @Field("customer_remarks") customer_remarks: String,
//        @Field("custom_customer_signature") custom_customer_signature: String,
//        @Field("raised_by") raised_by: String,
//        @Field("status") status: String,
//        @Field("priority") priority: String,
//        @Field("issue_type") issue_type: String,
//        @Field("description") description: String,
//        @Field("resolution_details") resolution_details: String,

    ): Response<IssueItem>

    //
    @GET("/api/resource/Issue/{id}")
    fun getEditIssue(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<Edit>

    //
//    @FormUrlEncoded
    @PUT("/api/resource/Issue/{id}")
    suspend fun editIssue(
        @Header("Authorization") token: String,
        @Path("id") id: String,
//        @Field("name") name : String,
//        @Field("subject") subject: String,
//        @Field("customer") customer: String,
//        @Field("custom_department") custom_department: String,
//        @Field("custom_mob_number") custom_mob_number: String,
//        @Field("custom_location") custom_location:String,
//        @Field("custom_office") custom_office: String,
//        @Field("custom_contact_person") custom_contact_person: String,
//        @Field("custom_assign_user") custom_assign_user: String,
//        @Field("action_taken") action_taken: String,
//        @Field("customer_remarks") customer_remarks: String,
//        @Field("custom_customer_signature") custom_customer_signature: String,
//        @Field("raised_by") raised_by: String,
//        @Field("status") status: String,
//        @Field("priority") priority: String,
//        @Field("issue_type") issue_type: String,
//        @Field("description") description: String,
//        @Field("resolution_details") resolution_details: String,
        @Body editIssueRequestModel: IssueItem

    ): Response<Edit>

    @GET("/api/resource/Item/{item}")
    suspend fun getItemName(
        @Header("Authorization") token: String,
        @Path("item") id: String
    ): Response<ItemNameItem>

    @GET("/api/resource/Issue/{id}")
    suspend fun getIssueItem(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Edit>

    //
    @DELETE("/api/resource/Issue/{id}")
    suspend fun deleteIssue(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<DeleteIssueModel>
}


//@GET("api/resource/Customer")
//suspend fun getAllUsers(@Header("Authorization")token: String): Response<CustomerData>
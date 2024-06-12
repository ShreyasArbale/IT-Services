package com.example.maintenance

import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeData
import com.example.maintenance.SalesPerson.model_sales_person.SalesPersonData
import com.example.maintenance.calendar.model_scheduled_date.ScheduledDate
import com.example.maintenance.department.model_department.DepartmentItemData
import com.example.maintenance.department.model_department.DepartmentItemList
import com.example.maintenance.issue_type.model_issue_type.IssueTypeItemList
import com.example.maintenance.location.model_location.LocationDataItem
import com.example.maintenance.login.model_login.LoginResponse
import com.example.maintenance.office.model_office.OfficeData
import com.example.maintenance.office.model_office.OfficeItem
import com.example.maintenance.resoledBy.model_reslovedBy.ResolvedBy
import com.example.maintenance.user.model_user.UserItem
import com.example.trial2.AddIssue.model.CustomerData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiInterface {

//    https://mobilecrm.erpdata.in/api/method/helpdesk.helpdesk_env.app.login?usr=shreyas.arbale@erpdata.in&pwd=Shreyas@8211

    //    @FormUrlEncoded
    @GET("/api/method/helpdesk.helpdesk_env.app.login")
    fun userLogin(

        @Query("usr") user1: String,
        @Query("pwd") password1: String
    ): Call<LoginResponse>

    @GET("/api/resource/Customer")
    suspend fun getCustomer(@Header("Authorization") token: String): Response<CustomerData>

    //    api/resource/Item?fields=[%22item_code%22,%22item_name%22]
    @GET("/api/resource/Item?fields=[\"item_code\",\"item_name\",\"name\"]\n&limit_page_length=9999")
    suspend fun getItemCode(@Header("Authorization") token: String): Response<ItemCodeData>

    @GET("/api/resource/Sales Person")
    suspend fun getSalesPerson(@Header("Authorization") token: String): Response<SalesPersonData>

    @GET("/api/resource/User")
    suspend fun getResolvedBy(@Header("Authorization") token: String): Response<ResolvedBy>

    //    get request for Schedule date
    @GET("/api/method/helpdesk.helpdesk_env.app.get_schedule_data?fields=[\"customer\",\"item_code\",\"sales_person\",\"completion_status\",\"scheduled_date\"]")
    suspend fun getScheduledDate(@Header("Authorization") token: String): Response<ScheduledDate>

    @GET("/api/resource/Issue Type")
    suspend fun getIssueType(@Header("Authorization") token: String): Response<IssueTypeItemList>

    @GET("/api/resource/Department")
    suspend fun getDepartment(@Header("Authorization") token: String): Response<DepartmentItemList>

    @GET("/api/resource/Location")
    suspend fun getLocation(@Header("Authorization") token: String): Response<LocationDataItem>

    @GET("/api/resource/Office")
    suspend fun getOffice(@Header("Authorization") token: String): Response<OfficeItem>

    @GET("/api/resource/User")
    suspend fun getUser(@Header("Authorization") token: String): Response<UserItem>
}
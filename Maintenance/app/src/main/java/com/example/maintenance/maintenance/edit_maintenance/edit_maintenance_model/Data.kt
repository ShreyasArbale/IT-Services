package com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model

data class Data(
    val address_display: String,
    val company: String,
    val creation: String,
    val customer: String,
    val customer_address: String,
    val customer_group: String,
    val customer_name: String,
    val docstatus: Int,
    val doctype: String,
    val idx: Int,
    val items: ArrayList<Item>,
    val modified: String,
    val modified_by: String,
    val name: String,
    val naming_series: String,
    val owner: String,
    val schedules: ArrayList<Schedule>,
    val status: String,
    val territory: String,
    val transaction_date: String
)
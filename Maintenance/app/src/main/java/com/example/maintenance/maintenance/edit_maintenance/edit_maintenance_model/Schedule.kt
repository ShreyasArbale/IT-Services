package com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model

data class Schedule(
    val completion_status: String,
    val creation: String,
    val docstatus: Int,
    val doctype: String,
    val idx: Int,
    val item_code: String,
    val item_name: String,
    val item_reference: String,
    val modified: String,
    val modified_by: String,
    val name: String,
    val owner: String,
    val parent: String,
    val parentfield: String,
    val parenttype: String,
    val sales_person: String,
    val scheduled_date: String
)
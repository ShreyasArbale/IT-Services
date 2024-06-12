package com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_edit_visit_maintenance

data class Data(
    val company: String,
    val completion_status: String,
    val creation: String,
    val customer: String,
    val docstatus: Int,
    val doctype: String,
    val idx: Int,
    val maintenance_type: String,
    val mntc_date: String,
    val mntc_time: String,
    val modified: String,
    val modified_by: String,
    val name: String,
    val naming_series: String,
    val owner: String,
    val purposes: ArrayList<Purpose>,
    val status: String
)
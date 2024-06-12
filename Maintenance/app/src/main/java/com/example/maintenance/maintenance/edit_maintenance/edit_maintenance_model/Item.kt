package com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model

data class Item(
    val creation: String,
    val description: String,
    val docstatus: Int,
    val doctype: String,
    val end_date: String,
    val idx: Int,
    val item_code: String,
    val item_name: String,
    val modified: String,
    val modified_by: String,
    val name: String,
    val no_of_visits: Int,
    val owner: String,
    val parent: String,
    val parentfield: String,
    val parenttype: String,
    val periodicity: String,
    val sales_person: String,
    val start_date: String
)
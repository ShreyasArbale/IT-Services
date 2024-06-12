package com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_edit_visit_maintenance

data class Purpose(
    val creation: String,
    val description: String,
    val docstatus: Int,
    val doctype: String,
    val idx: Int,
    val item_code: String,
    val item_name: String,
    val modified: String,
    val modified_by: String,
    val name: String,
    val owner: String,
    val parent: String,
    val parentfield: String,
    val parenttype: String,
    val service_person: String,
    val work_done: String
)
package com.example.maintenance.issue.model_item_name

data class Uom(
    val conversion_factor: Double,
    val creation: String,
    val docstatus: Int,
    val doctype: String,
    val idx: Int,
    val modified: String,
    val modified_by: String,
    val name: String,
    val parent: String,
    val parentfield: String,
    val parenttype: String,
    val uom: String
)
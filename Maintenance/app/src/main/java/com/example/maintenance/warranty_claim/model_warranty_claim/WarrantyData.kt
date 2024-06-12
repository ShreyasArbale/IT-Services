package com.example.maintenance.warranty_claim.model_warranty_claim

data class WarrantyData(
    val amc_expiry_date: String,
    val company: String,
    val complaint: String,
    val complaint_date: String,
    val creation: String,
    val customer: String,
    val customer_group: String,
    val customer_name: String,
    val description: String,
    val docstatus: Int,
    val doctype: String,
    val idx: Int,
    val item_code: String,
    val item_name: String,
    val modified: String,
    val modified_by: String,
    val name: String,
    val naming_series: String,
    val owner: String,
    val resolution_date: String,
    val resolved_by: String,
    val status: String,
    val territory: String,
    val warranty_amc_status: String,
    val warranty_expiry_date: String
)
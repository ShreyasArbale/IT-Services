package com.example.maintenance.warranty_claim.add_warranty_claim.add_warranty_claim_model

data class AddWarrantyClaimData(
    val amc_expiry_date: String,
    val complaint: String,
    val complaint_date: String,
    val customer: String,
    val item_code: String,
    val resolution_date: String,
    val resolution_details: String,
    val resolved_by: String,
    val warranty_amc_status: String,
    val warranty_expiry_date: String
)
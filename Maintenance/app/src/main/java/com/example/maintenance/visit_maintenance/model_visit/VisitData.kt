package com.example.maintenance.visit_maintenance.model_visit

data class VisitData(
    val completion_status: String,
    val customer: String,
    val maintenance_type: String,
    val name: String,
    val status: String
)
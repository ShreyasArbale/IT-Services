package com.example.maintenance.calendar.model_scheduled_date

data class Message(
    val completion_status: String,
    val customer: String,
    val item_code: String,
    val sales_person: String,
    val scheduled_date: String
)
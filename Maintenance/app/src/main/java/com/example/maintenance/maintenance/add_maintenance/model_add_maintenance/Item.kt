package com.example.maintenance.maintenance.add_maintenance.model_add_maintenance

data class Item(
    val item_code: String,
    val start_date: String,
    val end_date: String,
    val periodicity: String,
    val no_of_visits: Int,
    val sales_person: String
)
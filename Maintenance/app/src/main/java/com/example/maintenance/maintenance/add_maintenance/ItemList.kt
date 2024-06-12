package com.example.maintenance.maintenance.add_maintenance

data class ItemList(
    val itemCode: String,
    val itemName: String,
    val startDate: String,
    val endDate: String,
    val periodicity: String,
    val visits: String,
    val salesPerson: String,
)

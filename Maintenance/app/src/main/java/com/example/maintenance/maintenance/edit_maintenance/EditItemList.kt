package com.example.maintenance.maintenance.edit_maintenance

data class EditItemList(
    val itemCode: String,
    val startDate: String,
    val endDate: String,
    val periodicity: String,
    val visits: String,
    val salesPerson: String,
)

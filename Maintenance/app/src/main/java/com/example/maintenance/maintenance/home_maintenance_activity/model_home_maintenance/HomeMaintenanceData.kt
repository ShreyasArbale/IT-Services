package com.example.maintenance.maintenance.home_maintenance_activity.model_home_maintenance

import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.Item

data class HomeMaintenanceData(
    val customer: String,
    val name: String,
    val status: String,
    val transaction_date: String,
    val items: Item,
)
package com.example.maintenance.maintenance.home_maintenance_activity.model_home_maintenance

data class MaintenanceData(
    val `data`: ArrayList<HomeMaintenanceData>
)

data class MaintenanceDataUser(
    val `data`: HomeMaintenanceData
)
package com.example.maintenance.maintenance.edit_maintenance

interface MaintenanceScheduleListener {
    fun onMaintenanceScheduleClick(
        name: String,
        itemCode: String,
        startDate: String,
        endDate: String,
        periodicity: String,
        noOfVisits: Int,
        salesPerson: String
    )

//    fun onMaintenanceDeleteItemClick(
//        name: String
//    )
}
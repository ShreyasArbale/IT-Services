package com.example.maintenance.maintenance.edit_maintenance

interface MaintenanceItemListener {
    fun onMaintenanceItemClick(
        name: String,
        itemCode: String,
        startDate: String,
        endDate: String,
        periodicity: String,
        noOfVisits: String,
        salesPerson: String
    )

    fun onMaintenanceDeleteItemClick(
        name: String
    )
}
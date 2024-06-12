package com.example.maintenance.visit_maintenance.edit_visit_maintenance

interface VisitItemListener {

    fun onVisitItemClick(
        name: String,
        itemCode: String,
        servicePerson: String,
        description: String,
        workDone: String
    )

    fun onVisitMaintenanceDeleteItemClick(
        name: String
    )
}
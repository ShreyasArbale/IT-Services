package com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance

data class Data(
    var completion_status: String,
    var customer: String,
    var maintenance_type: String,
    var mntc_date: String,
    var purposes: ArrayList<Purpose>
)
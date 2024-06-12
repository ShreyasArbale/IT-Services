package com.example.maintenance.maintenance.add_maintenance.model_add_maintenance

data class Data(
    var customer: String,
    var items: List<Item>,
//    var status: String,
    var transaction_date: String
)
package com.example.maintenance.issue.edit_issue

interface IssueItemListener {
    fun onIssueItemClick(
        itemCode: String,
        itemName: String
    )

    fun onIssueDeleteItemClick(
        name: String
    )
}
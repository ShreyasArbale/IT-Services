package com.example.maintenance.issue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.RecyclerItemIssueBinding
import com.example.maintenance.issue.model_issue.IssueItem

class IssueAdapter(
    private val userList: ArrayList<IssueItem>,
    private val issueListener: IssueListener
) :
    RecyclerView.Adapter<IssueAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerItemIssueBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerItemIssueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.binding.apply {
            tvId.text = "Id: ${currentItem.name}"
            tvSubject.text = "Subject: ${currentItem.subject}"
            tvStatus.text = "Status: ${currentItem.status}"
            tvPriority.text = "Priority: ${currentItem.priority}"
            tvRaisedBy.text = "Email: ${currentItem.raised_by}"
        }
        holder.binding.root.setOnClickListener {
            issueListener.onIssueClick(currentItem.name)
        }
    }
}
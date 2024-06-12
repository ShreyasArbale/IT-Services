package com.example.maintenance.issue.edit_issue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerItemNameBinding
import com.example.maintenance.issue.edit_issue.model_edit_issue.CustomMaterialUsed

class EditIssueItemAdapter(
    private val issueItemList: ArrayList<CustomMaterialUsed>,
//    private val itemListListener: IssueItemListener
) :
    RecyclerView.Adapter<EditIssueItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HorizontalRecyclerItemNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemNameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return issueItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = issueItemList[position]
        holder.binding.apply {
            tvItemCode.text = currentItem.used_item
            tvItemName.text = currentItem.used_item_name
        }
//        holder.binding.root.setOnClickListener {
//            itemListListener.onMaintenanceItemClick(currentItem.name)
//        }



//        holder.binding.btnEdit.setOnClickListener {
//            itemListListener.onIssueItemClick(
//                currentItem.used_item,
//                currentItem.used_item_name,
//            )
//        }
//
//        holder.binding.btnDelete.setOnClickListener {
//            itemListListener.onIssueDeleteItemClick(currentItem.name)
//        }
    }
}
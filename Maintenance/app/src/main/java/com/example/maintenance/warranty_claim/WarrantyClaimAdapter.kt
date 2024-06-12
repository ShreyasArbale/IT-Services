package com.example.maintenance.warranty_claim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.RecyclerItemWarrantyClaimBinding
import com.example.maintenance.warranty_claim.model_warranty_claim.WarrantyData

class WarrantyClaimAdapter (
    private val warrantyList: ArrayList<WarrantyData>,
    private val warrantyListener: WarrantyListListener
) :
    RecyclerView.Adapter<WarrantyClaimAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerItemWarrantyClaimBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerItemWarrantyClaimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return warrantyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = warrantyList[position]
        holder.binding.apply {
            tvName.text = "Id: ${currentItem.name}"
            tvCustomerName.text = "Customer: ${currentItem.customer}"
            tvItemCode.text = "Item Code: ${currentItem.item_code}"
            tvIssueDate.text = "Issue Date: ${currentItem.complaint_date}"
            tvStatus.text = "Status: ${currentItem.status}"

        }
        holder.binding.root.setOnClickListener {
            warrantyListener.onWarrantyClick(currentItem.name)
        }
    }
}
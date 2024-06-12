package com.example.maintenance.visit_maintenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.RecyclerItemVisitMaintenanceBinding
import com.example.maintenance.visit_maintenance.model_visit.VisitData

class VisitMaintenanceAdapter(
    private val visitMaintenanceList: ArrayList<VisitData>,
    private val visitListener: VisitListListener
) :
    RecyclerView.Adapter<VisitMaintenanceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerItemVisitMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerItemVisitMaintenanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return visitMaintenanceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = visitMaintenanceList[position]
        holder.binding.apply {
            tvName.text = "Id: ${currentItem.name}"
            tvCustomerName.text = "Customer: ${currentItem.customer}"
            tvCompletionStatus.text = "Completion Status: ${currentItem.completion_status}"
            tvMaintenanceType.text = "Maintenance Type: ${currentItem.maintenance_type}"
            tvStatus.text = "Status: ${currentItem.status}"

        }
        holder.binding.root.setOnClickListener {
            visitListener.onVisitClick(currentItem.name)
        }
    }
}
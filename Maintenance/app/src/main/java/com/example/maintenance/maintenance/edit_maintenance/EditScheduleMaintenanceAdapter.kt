package com.example.maintenance.maintenance.edit_maintenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerScheduleMaintenanceBinding
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.Schedule

class EditScheduleMaintenanceAdapter(
    private val maintenanceScheduleList: ArrayList<Schedule>
) :
    RecyclerView.Adapter<EditScheduleMaintenanceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HorizontalRecyclerScheduleMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerScheduleMaintenanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return maintenanceScheduleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = maintenanceScheduleList[position]
        holder.binding.apply {
            tvNo.text = "${currentItem.idx}"
            tvItemCode.text = "${currentItem.item_code}"
            tvScheduleData.text = "${currentItem.scheduled_date}"
            tvCompletionStatus.text = "${currentItem.completion_status}"
            tvSalesPerson.text = "${currentItem.sales_person}"
        }
    }
}
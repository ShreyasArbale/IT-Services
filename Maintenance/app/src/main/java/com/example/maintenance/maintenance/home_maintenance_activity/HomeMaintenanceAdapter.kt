package com.example.maintenance.maintenance.home_maintenance_activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.maintenance.MaintenanceListener
import com.example.maintenance.databinding.RecyclerItemMaintenanceBinding
import com.example.maintenance.maintenance.home_maintenance_activity.model_home_maintenance.HomeMaintenanceData

class HomeMaintenanceAdapter(private val maintenanceList: ArrayList<HomeMaintenanceData>,
                             private val itemListListener: MaintenanceListener
):
    RecyclerView.Adapter<HomeMaintenanceAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: RecyclerItemMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerItemMaintenanceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return maintenanceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = maintenanceList[position]
        holder.binding.apply {
            tvName.text = "Id: ${currentItem.name}"
            tvCustomerName.text = "Customer: ${currentItem.customer}"
            tvStatus.text = "Status: ${currentItem.status}"
            tvTransactionData.text = "Transaction Date: ${currentItem.transaction_date}"
        }
        holder.binding.root.setOnClickListener {
            itemListListener.onMaintenanceClick(currentItem.name)
        }
    }
}
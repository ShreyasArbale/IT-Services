package com.example.maintenance.maintenance.edit_maintenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerItemMaintenanceBinding
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.Item

class EditItemMaintenanceAdapter(
    private val maintenanceItemList: ArrayList<Item>,
    private val itemListListener: MaintenanceItemListener
) :
    RecyclerView.Adapter<EditItemMaintenanceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HorizontalRecyclerItemMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemMaintenanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return maintenanceItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = maintenanceItemList[position]
        holder.binding.apply {
            tvNo.text = "${currentItem.idx}"
            tvItemCode.text = "${currentItem.item_code}"
            tvStartData.text = "${currentItem.start_date}"
            tvEndData.text = "${currentItem.end_date}"
            tvPeriodicity.text = "${currentItem.periodicity}"
            tvVisits.text = "${currentItem.no_of_visits}"
            tvSalesPerson.text = "${currentItem.sales_person}"
        }
//        holder.binding.root.setOnClickListener {
//            itemListListener.onMaintenanceItemClick(currentItem.name)
//        }
        holder.binding.btnEdit.setOnClickListener {
            itemListListener.onMaintenanceItemClick(
                currentItem.name,
                currentItem.item_code,
                currentItem.start_date,
                currentItem.end_date,
                currentItem.periodicity,
                currentItem.no_of_visits.toString(),
                currentItem.sales_person
            )
        }

        holder.binding.btnDelete.setOnClickListener {
            itemListListener.onMaintenanceDeleteItemClick(currentItem.name)
        }
    }
}
package com.example.maintenance.visit_maintenance.edit_visit_maintenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerItemVisitMaintenanceBinding
import com.example.maintenance.visit_maintenance.VisitListListener
import com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_edit_visit_maintenance.Purpose

class EditVisitMaintenanceAdapter(
    private val visitMaintenanceItemList: ArrayList<Purpose>,
    private val itemListListener: VisitItemListener
) :
    RecyclerView.Adapter<EditVisitMaintenanceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HorizontalRecyclerItemVisitMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemVisitMaintenanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return visitMaintenanceItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = visitMaintenanceItemList[position]
        holder.binding.apply {
            tvItemCode.text = "${currentItem.item_code}"
            tvSalesPerson.text = "${currentItem.service_person}"
            tvDescription.text = "${currentItem.description}"
            tvWorkDone.text = "${currentItem.work_done}"
        }

        holder.binding.btnEdit.setOnClickListener {
            itemListListener.onVisitItemClick(
                currentItem.name,
                currentItem.item_code,
                currentItem.service_person,
                currentItem.description,
                currentItem.work_done
            )
        }

        holder.binding.btnDelete.setOnClickListener {
            itemListListener.onVisitMaintenanceDeleteItemClick(currentItem.name)
        }
    }
}
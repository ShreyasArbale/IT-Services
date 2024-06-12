package com.example.maintenance.visit_maintenance.add_visit_maintenance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerItemVisitMaintenanceBinding

class VisitListAdapter (private val visitItemList: ArrayList<VisitItemList>):
    RecyclerView.Adapter<VisitListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: HorizontalRecyclerItemVisitMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemVisitMaintenanceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return visitItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = visitItemList[position]

        holder.binding.apply {
            tvItemCode.text = currentItem.itemCode
            tvSalesPerson.text = currentItem.salesPerson
            tvDescription.text = currentItem.description
            tvWorkDone.text = currentItem.work_done
        }
    }

    fun addVisitItem(item: VisitItemList){
        Log.d("myapp", "item added to list")
        this.visitItemList.add(item)
        notifyDataSetChanged()
    }
}
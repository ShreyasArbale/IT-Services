package com.example.maintenance.visit_maintenance.edit_visit_maintenance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.DialogAddItemMaintenanceBinding
import com.example.maintenance.databinding.DialogEditItemMaintenanceBinding
import com.example.maintenance.databinding.DialogEditItemVisitMaintenanceBinding
import com.example.maintenance.databinding.HorizontalRecyclerItemMaintenanceBinding
import com.example.maintenance.databinding.HorizontalRecyclerItemVisitMaintenanceBinding
import com.example.maintenance.maintenance.add_maintenance.ItemList

class EditVisitItemListAdapter(private val itemList: ArrayList<EditVisitItemList>):
    RecyclerView.Adapter<EditVisitItemListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: HorizontalRecyclerItemVisitMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemVisitMaintenanceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        lateinit var dialogBinding : DialogEditItemVisitMaintenanceBinding

        holder.binding.apply {
            tvItemCode.text = currentItem.itemCode
            tvSalesPerson.text = currentItem.salesPerson
            tvDescription.text = currentItem.description
            tvWorkDone.text = currentItem.workDone
        }
    }

    fun editVisitItem(item: EditVisitItemList){
        Log.d("myapp", "item edited to list")
        this.itemList.add(item)
        notifyDataSetChanged()
        Log.d("myapp", itemList.add(item).toString())
    }

}
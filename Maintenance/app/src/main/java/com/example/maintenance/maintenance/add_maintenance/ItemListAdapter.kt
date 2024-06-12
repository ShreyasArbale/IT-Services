package com.example.maintenance.maintenance.add_maintenance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.DialogAddItemMaintenanceBinding
import com.example.maintenance.databinding.HorizontalRecyclerItemMaintenanceBinding

class ItemListAdapter (private val itemList: ArrayList<ItemList>):
    RecyclerView.Adapter<ItemListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: HorizontalRecyclerItemMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemMaintenanceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        lateinit var dialogBinding : DialogAddItemMaintenanceBinding

        holder.binding.apply {
            tvItemCode.text = currentItem.itemCode
            tvStartData.text = currentItem.startDate
            tvEndData.text = currentItem.endDate
            tvPeriodicity.text = currentItem.periodicity
            tvVisits.text = currentItem.visits
            tvSalesPerson.text = currentItem.salesPerson
        }
    }

    fun addItem(item: ItemList){
        Log.d("myapp", "item added to list")
        this.itemList.add(item)
        notifyDataSetChanged()
    }

}
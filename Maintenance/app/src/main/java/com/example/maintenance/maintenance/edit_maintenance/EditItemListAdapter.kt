package com.example.maintenance.maintenance.edit_maintenance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.DialogAddItemMaintenanceBinding
import com.example.maintenance.databinding.DialogEditItemMaintenanceBinding
import com.example.maintenance.databinding.HorizontalRecyclerItemMaintenanceBinding
import com.example.maintenance.maintenance.add_maintenance.ItemList

class EditItemListAdapter( private val itemList: ArrayList<EditItemList>):
    RecyclerView.Adapter<EditItemListAdapter.ViewHolder>(){

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
        lateinit var dialogBinding : DialogEditItemMaintenanceBinding

        holder.binding.apply {
            tvItemCode.text = currentItem.itemCode
            tvStartData.text = currentItem.startDate
            tvEndData.text = currentItem.endDate
            tvPeriodicity.text = currentItem.periodicity
            tvVisits.text = currentItem.visits
            tvSalesPerson.text = currentItem.salesPerson
        }
    }

    fun editItem(item: EditItemList, position: Int){
        Log.d("myapp", "item edited to list")
        this.itemList.add(item)
//        this.itemList[position] = item
        Log.d("my this", this.itemList[position].toString())
//        notifyDataSetChanged()
        notifyItemChanged(position)
        Log.d("myapp", itemList.add(item).toString())
    }

}
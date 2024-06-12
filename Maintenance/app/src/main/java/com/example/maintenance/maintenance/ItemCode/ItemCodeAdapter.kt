package com.example.maintenance.maintenance.ItemCode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.ItemCodeSpinnerLayoutBinding
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.databinding.ItemSpinnerLayoutBinding

class ItemCodeAdapter (private val itemList: ArrayList<ItemCodeDataItem>): RecyclerView.Adapter<ItemCodeAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemCodeSpinnerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCodeSpinnerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemItem = itemList[position]
        holder.binding.apply {
            tvItemCode.text = "Item Code: ${itemItem.name}"
            tvItemName.text = "Item Name: ${itemItem.item_code}"
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}
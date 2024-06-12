package com.example.maintenance.issue.add_issue

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerItemNameBinding
import com.example.maintenance.issue.model_item_name.ItemNameList

class ItemNameAdapter (private val itemNameList: ArrayList<ItemNameList>):
    RecyclerView.Adapter<ItemNameAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: HorizontalRecyclerItemNameBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemNameBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemNameList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemNameList[position]

        holder.binding.apply {
            tvItemCode.text = currentItem.itemCode
            tvItemName.text = currentItem.itemName
        }
    }

    fun addItem(item: ItemNameList){
        Log.d("myapp", "item added to list")
        this.itemNameList.add(item)
        notifyDataSetChanged()
    }

}
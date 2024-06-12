package com.example.maintenance.issue.edit_issue

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.HorizontalRecyclerItemNameBinding
import com.example.maintenance.issue.add_issue.model_add_issue.CustomMaterialUsed

class EditIssueItemListAdapter ( private val itemList: ArrayList<EditIssueItemList>):
    RecyclerView.Adapter<EditIssueItemListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: HorizontalRecyclerItemNameBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizontalRecyclerItemNameBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.binding.apply {
            tvItemCode.text = currentItem.itemCode
            tvItemName.text = currentItem.itemName
        }
    }

    fun editItem(item: EditIssueItemList, position: Int){
        Log.d("myapp", "item edited to list")
        this.itemList.add(item)
//        this.itemList[position] = item
        Log.d("my this", this.itemList[position].toString())
//        notifyDataSetChanged()
        notifyItemChanged(position)
        Log.d("myapp", itemList.add(item).toString())
    }
}
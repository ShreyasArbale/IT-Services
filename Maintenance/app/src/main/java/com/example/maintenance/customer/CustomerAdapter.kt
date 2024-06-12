package com.example.maintenance.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.ItemSpinnerLayoutBinding
import com.example.maintenance.customer.model_customer.CustomerDataItem

class CustomerAdapter (private val customerList: ArrayList<CustomerDataItem>): RecyclerView.Adapter<CustomerAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemSpinnerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSpinnerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = customerList[position]
        holder.binding.apply {
            tvName.text = "Customer name: ${currentItem.name}"
        }
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

}
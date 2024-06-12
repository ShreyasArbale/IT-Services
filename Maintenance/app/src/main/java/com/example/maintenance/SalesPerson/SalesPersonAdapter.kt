package com.example.maintenance.SalesPerson

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.SalesPerson.model_sales_person.SalesPersonDataItem
import com.example.maintenance.databinding.ItemSpinnerLayoutBinding

class SalesPersonAdapter (private val personList: ArrayList<SalesPersonDataItem>): RecyclerView.Adapter<SalesPersonAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemSpinnerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSpinnerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personItem = personList[position]
        holder.binding.apply {
            tvName.text = "Sales Person: ${personItem.name}"
        }
    }

    override fun getItemCount(): Int {
        return personList.size
    }

}
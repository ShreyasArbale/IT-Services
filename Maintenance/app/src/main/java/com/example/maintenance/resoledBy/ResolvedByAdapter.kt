package com.example.maintenance.resoledBy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.databinding.ItemSpinnerLayoutBinding
import com.example.maintenance.resoledBy.model_reslovedBy.ResolvedByData

class ResolvedByAdapter (private val resolvedByList: ArrayList<ResolvedByData>):
    RecyclerView.Adapter<ResolvedByAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemSpinnerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSpinnerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = resolvedByList[position]
        holder.binding.apply {
            tvName.text = "Resolved By: ${currentItem.name}"
        }
    }

    override fun getItemCount(): Int {
        return resolvedByList.size
    }

}
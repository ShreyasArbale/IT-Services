package com.example.maintenance.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.calendar.model_scheduled_date.Message
import com.example.maintenance.databinding.ActivityCalendarBinding
import com.example.maintenance.databinding.ScheduledDateLayoutBinding
import java.util.Date

class ScheduledDateAdapter(private val scheduledDateList: ArrayList<Message>, private val selectedDate : String

): RecyclerView.Adapter<ScheduledDateAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ScheduledDateLayoutBinding, val cBinding: ActivityCalendarBinding) :
        RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ScheduledDateLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            ActivityCalendarBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        )
    }

    override fun getItemCount(): Int {
        return scheduledDateList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = scheduledDateList[position]
        holder.binding.apply {
//            tvScheduleDate.text = "${currentItem.scheduled_date}"
        }
    }

}
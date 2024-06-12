package com.example.maintenance.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.calendar.model_scheduled_date.Message
import com.example.maintenance.databinding.DialogCalendarItemBinding

class ScheduledDateItemAdapter(
    private val scheduledDateList: ArrayList<Message>,
    private val pos: ArrayList<Int>,
    private var selectedDate: String

) : RecyclerView.Adapter<ScheduledDateItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DialogCalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DialogCalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
//        Log.d("my pos", pos.toString())
//        var count = 0
//        for (i in 0 until pos.size){
//            if (scheduledDateList[i].scheduled_date == selectedDate){
//                count++
//                Log.d("my count1", scheduledDateList[i].scheduled_date)
//                Log.d("my count1", selectedDate)
//            }
//        }
//        Log.d("my count1", count.toString())
        return pos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        for (i in 0 until pos.size) {
//            val currentItem = scheduledDateList[pos[i]]
//            Log.d("my current", currentItem.toString())
//
////            if (scheduledDateList[pos[i]].scheduled_date == selectedDate) {
//                holder.binding.apply {
//                    tvCustomerName.text = "${currentItem.customer}"
//                    tvItemCode.text = "Item Code: ${currentItem.item_code}"
//                    tvScheduleDate.text = "Schedule Date: ${currentItem.scheduled_date}"
//                    tvCompletionStatus.text = "Status: ${currentItem.completion_status}"
//                }
//            Log.d("my item details", "${currentItem.customer}, ${currentItem.item_code}, ${currentItem.scheduled_date}, ${currentItem.completion_status}")
////            }
//        }

        with(holder) {
            with(position) {
                val currentItem = scheduledDateList[pos[this]]
                binding.tvCustomerName.text = "${currentItem.customer}"
                binding.tvItemCode.text = "Item Code: ${currentItem.item_code}"
                binding.tvScheduleDate.text = "Schedule Date: ${currentItem.scheduled_date}"
                binding.tvCompletionStatus.text = "Status: ${currentItem.completion_status}"
                Log.d("my item details", "${currentItem.customer}, ${currentItem.item_code}, ${currentItem.scheduled_date}, ${currentItem.completion_status}")
            }
        }

    }

}
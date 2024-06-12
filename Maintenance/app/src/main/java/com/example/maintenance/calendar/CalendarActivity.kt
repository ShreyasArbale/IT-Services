package com.example.maintenance.calendar

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maintenance.R
import com.example.maintenance.calendar.model_scheduled_date.Message
import com.example.maintenance.databinding.ActivityCalendarBinding
import com.example.maintenance.storage.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding

    private lateinit var scheduledDateList: ArrayList<Message>
    private lateinit var rvAdapter: ScheduledDateAdapter

    private lateinit var rvItemAdapter: ScheduledDateItemAdapter
    private lateinit var selectedDate: String

    private var mProgressDialog: Dialog? = null

    private val allDates: ArrayList<String> = arrayListOf()

    private lateinit var  baseUrl : String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCalendarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        selectedDate = ""

        var nm: ArrayList<Int> = arrayListOf()
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            if (dayOfMonth in 1..9 && month in 1..9) {
                selectedDate = "$year-0${month + 1}-0$dayOfMonth"
            } else if (dayOfMonth in 1..9 && month in 10..12) {
                selectedDate = "$year-${month + 1}-0$dayOfMonth"
            } else if (dayOfMonth in 10..31 && month in 1..9) {
                selectedDate = "$year-0${month + 1}-$dayOfMonth"
            } else if (dayOfMonth in 10..31 && month in 10..12) {
                selectedDate = "$year-${month + 1}-$dayOfMonth"
            }

            nm.clear()
            for (i in 0 until scheduledDateList.size) {

                if (scheduledDateList[i].scheduled_date == selectedDate) {
                    binding.calendarViewNoSchedule.visibility = View.INVISIBLE
                    binding.rvCalenderDetails.visibility = View.VISIBLE

                    Log.d("my sel date3", scheduledDateList[i].scheduled_date)
                    Log.d("my sel date4", selectedDate)

                    nm.add(i)
                    Log.d("my sel date4", nm.toString())

                }
            }
            infoDetail(nm)
            if (nm.isEmpty()) {
                binding.calendarViewNoSchedule.visibility = View.VISIBLE
                binding.rvCalenderDetails.visibility = View.GONE
            }
        }
        recyclerView()

        binding.imageButtonBack.setOnClickListener {
            finish()
        }
    }

    private fun infoDetail(nm: ArrayList<Int>) {
        showCustomProgressDialog()

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        super.onResume()

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                ScheduledDateRetrofitInstance.setBaseUrl(baseUrl)
                ScheduledDateRetrofitInstance.api.getScheduledDate(token)
            } catch (e: IOException) {
//                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                Log.d("myapp", "${e} ${e.message}")
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app", response.toString())
            if (response.isSuccessful && response.body() != null) {
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.message

//                    Log.d("my app", result[0].name)
                    scheduledDateList = result

                    binding.rvCalenderDetails.apply {

                        rvItemAdapter = ScheduledDateItemAdapter(scheduledDateList, nm, selectedDate)

                        Log.d("sel date", selectedDate)
                        adapter = rvItemAdapter
                        layoutManager = LinearLayoutManager(this@CalendarActivity)

                    }
//                    Toast.makeText(this@CalendarActivity, "array : $nm", Toast.LENGTH_SHORT)
//                        .show()
                    Log.d("my nm", nm.toString())
                }
            } else {
                hideProgressDialog()
                Toast.makeText(this@CalendarActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            Log.d("myapp", "onResume called")

        }
    }


    private fun recyclerView() {
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                ScheduledDateRetrofitInstance.setBaseUrl(baseUrl)
                ScheduledDateRetrofitInstance.api.getScheduledDate(token)
            } catch (e: IOException) {
//                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                Log.d("myapp", "${e} ${e.message}")
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app", response.toString())
            if (response.isSuccessful && response.body() != null) {

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.message

                    scheduledDateList = result

                    scheduledDateList.forEach {
                        allDates.add(it.scheduled_date)
                    }
                    Log.d("my all dates", allDates.toString())


                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    for (dateStr in allDates) {
                        val date = sdf.parse(dateStr)
                        val calendar = Calendar.getInstance()
                        if (date != null) {
                            calendar.time = date
                            Log.d("my cal date", calendar.time.toString())
                        }
                        binding.calendarView.setDate(calendar.timeInMillis, false, false)
//                        binding.calendarView.date = date.time
                    }


                    binding.rvCalender.apply {
                        rvAdapter = ScheduledDateAdapter(scheduledDateList, selectedDate)
                        Log.d("sel date", selectedDate)
                        adapter = rvAdapter
                        layoutManager = LinearLayoutManager(this@CalendarActivity)
                    }
                }
            } else {
                Toast.makeText(this@CalendarActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            Log.d("myapp", "onResume called")
        }

    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

}

package com.example.maintenance.maintenance.home_maintenance_activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maintenance.R
import com.example.maintenance.calendar.CalendarActivity
import com.example.maintenance.databinding.ActivityHomeMaintenanceBinding
import com.example.maintenance.maintenance.MaintenanceListener
import com.example.maintenance.maintenance.add_maintenance.AddMaintenanceActivity
import com.example.maintenance.maintenance.edit_maintenance.EditMaintenanceActivity
import com.example.maintenance.maintenance.home_maintenance_activity.model_home_maintenance.HomeMaintenanceData
import com.example.maintenance.storage.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeMaintenanceActivity : AppCompatActivity(), MaintenanceListener {

    private lateinit var binding: ActivityHomeMaintenanceBinding
    private lateinit var rvAdapter: HomeMaintenanceAdapter

    private lateinit var maintenanceList: ArrayList<HomeMaintenanceData>

    private var mProgressDialog: Dialog? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        maintenanceList = arrayListOf()

        binding.flotAdd.setOnClickListener {
            val intent = Intent(this, AddMaintenanceActivity::class.java)
            startActivity(intent)
        }

        binding.imageButtonBack.setOnClickListener{
            finish()
        }

        binding.imageButtonCalendar.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        showCustomProgressDialog()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        super.onResume()

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                HomeMaintenanceRetrofitInstance.setBaseUrl(baseUrl)
                HomeMaintenanceRetrofitInstance.api.getMaintenance(token)
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
                    val result = response.body()!!.data

//                    Log.d("my app", result[0].name)
                    maintenanceList = result
                    binding.rvMain.apply {
                        rvAdapter = HomeMaintenanceAdapter(maintenanceList, this@HomeMaintenanceActivity)
                        adapter = rvAdapter
                        layoutManager = LinearLayoutManager(this@HomeMaintenanceActivity)
                        (layoutManager as LinearLayoutManager).reverseLayout = true
                        (layoutManager as LinearLayoutManager).stackFromEnd = true
                    }
                }
            }else{
               Log.d("Error", "Something went wrong")
//                hideProgressDialog()
            }
            Log.d("myapp", "onResume called")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    override fun onMaintenanceClick(id: String) {
        val intent = Intent(this@HomeMaintenanceActivity, EditMaintenanceActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
   }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog(){
        if (mProgressDialog != null){
            mProgressDialog!!.dismiss()
        }
    }

}
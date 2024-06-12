package com.example.maintenance.visit_maintenance

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maintenance.R
import com.example.maintenance.databinding.ActivityHomeVisitMaintenanceBinding
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.visit_maintenance.add_visit_maintenance.AddVisitMaintenanceActivity
import com.example.maintenance.visit_maintenance.edit_visit_maintenance.EditVisitMaintenanceActivity
import com.example.maintenance.visit_maintenance.model_visit.VisitData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeVisitMaintenanceActivity : AppCompatActivity(), VisitListListener{

    private lateinit var binding: ActivityHomeVisitMaintenanceBinding
    private lateinit var visitList: ArrayList<VisitData>
    private lateinit var rvVisitAdapter: VisitMaintenanceAdapter
    private var mProgressDialog: Dialog? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeVisitMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.flotAddVisit.setOnClickListener {
            val intent = Intent(this@HomeVisitMaintenanceActivity, AddVisitMaintenanceActivity::class.java)
            startActivity(intent)
        }

        binding.imageButtonBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        showCustomProgressDialog()
        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                VisitMaintenanceInstance.setBaseUrl(baseUrl)
                VisitMaintenanceInstance.api.getVisitMaintenance(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
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
                    Log.d("my app", result[0].name)
                    visitList = result as ArrayList<VisitData>
                    binding.rvVisit.apply {
                        rvVisitAdapter = VisitMaintenanceAdapter(visitList, this@HomeVisitMaintenanceActivity)
                        adapter = rvVisitAdapter
                        layoutManager = LinearLayoutManager(this@HomeVisitMaintenanceActivity)
                        (layoutManager as LinearLayoutManager).reverseLayout = true
                        (layoutManager as LinearLayoutManager).stackFromEnd = true
                    }
                }
            }
        }
    }

    override fun onVisitClick(id: String) {
        val intent = Intent(this@HomeVisitMaintenanceActivity, EditVisitMaintenanceActivity::class.java)
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
package com.example.maintenance.warranty_claim

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maintenance.R
import com.example.maintenance.databinding.ActivityHomeWarrantyClaimBinding
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.warranty_claim.add_warranty_claim.AddWarrantyClaimActivity
import com.example.maintenance.warranty_claim.edit_warranty_claim.EditWarrantyClaimActivity
import com.example.maintenance.warranty_claim.model_warranty_claim.WarrantyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeWarrantyClaimActivity : AppCompatActivity(), WarrantyListListener {

    private lateinit var binding: ActivityHomeWarrantyClaimBinding
    private lateinit var warrantyList: ArrayList<WarrantyData>
    private lateinit var rvWarrantyAdapter: WarrantyClaimAdapter
    private var mProgressDialog: Dialog? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityHomeWarrantyClaimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        warrantyList = arrayListOf()

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.flotAddWarranty.setOnClickListener {
            val intent = Intent(this@HomeWarrantyClaimActivity, AddWarrantyClaimActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        showCustomProgressDialog()
        super.onResume()
        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        GlobalScope.launch(Dispatchers.IO) {
            val token = SharedPreferenceManager.getInstance(applicationContext).getToken

            val response = try {
                WarrantyClaimRetrofitInstance.setBaseUrl(baseUrl)
                WarrantyClaimRetrofitInstance.api.getWarranty(token)
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
                    warrantyList = result
                    binding.rvWarranty.apply {
                        rvWarrantyAdapter = WarrantyClaimAdapter(warrantyList, this@HomeWarrantyClaimActivity)
                        adapter = rvWarrantyAdapter
                        layoutManager = LinearLayoutManager(this@HomeWarrantyClaimActivity)
                        (layoutManager as LinearLayoutManager).reverseLayout = true
                        (layoutManager as LinearLayoutManager).stackFromEnd = true
                    }
                }
            }
        }
    }

    override fun onWarrantyClick(name: String) {
        val intent = Intent(this@HomeWarrantyClaimActivity, EditWarrantyClaimActivity::class.java)
        intent.putExtra("id", name)
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
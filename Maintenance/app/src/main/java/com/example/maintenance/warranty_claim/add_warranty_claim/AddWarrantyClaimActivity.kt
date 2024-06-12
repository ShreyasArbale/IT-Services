package com.example.maintenance.warranty_claim.add_warranty_claim

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.databinding.ActivityAddWarrantyClaimBinding
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.resoledBy.ResoledByRetrofitInstance
import com.example.maintenance.resoledBy.model_reslovedBy.ResolvedByData
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.warranty_claim.WarrantyClaimRetrofitInstance
import com.example.maintenance.customer.model_customer.CustomerDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Calendar

class AddWarrantyClaimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWarrantyClaimBinding

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var resolvedByList: ArrayList<ResolvedByData>
    private lateinit var itemCodeList: ArrayList<ItemCodeDataItem>

    private lateinit var baseUrl: String

    private lateinit var selectedItem: ItemCodeDataItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddWarrantyClaimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        customer()
        issueDate()
        itemCode()
        warrantyStatus()
        warrantyExpiryDate()
        awsExpiryDate()
        resolutionDate()
        resoledBy()

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.buttonSubmit.setOnClickListener {

            if (binding.tvIssueDate.text!!.isEmpty()) {
                Toast.makeText(this, "Select Issue Date", Toast.LENGTH_SHORT).show()
            } else if (binding.etIssue.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Issue", Toast.LENGTH_SHORT).show()
            } else if (binding.tvWarrantyExpiryDate.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Warranty Expiry Date", Toast.LENGTH_SHORT).show()
            } else if (binding.tvAMCExpiryDate.text!!.isEmpty()) {
                Toast.makeText(this, "Enter AMC Expiry Date", Toast.LENGTH_SHORT).show()
            } else if (binding.tvResolutionDate.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Resolution Date", Toast.LENGTH_SHORT).show()
            } else if (binding.etResolutionDetails.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Resolution Details", Toast.LENGTH_SHORT).show()
            } else {

                GlobalScope.launch(Dispatchers.IO) {
                    val response = try {
                        WarrantyClaimRetrofitInstance.setBaseUrl(baseUrl)
                        WarrantyClaimRetrofitInstance.api.createWarrantyClaim(
                            token,
                            "",
                            customer = binding.spinnerCustomer.selectedItem.toString(),
                            complaint_date = binding.tvIssueDate.text.toString(),
                            complaint = binding.etIssue.text.toString(),
                            item_code = selectedItem.name,
                            warranty_amc_status = binding.spinnerWarrantyStatus.selectedItem.toString(),
                            warranty_expiry_date = binding.tvWarrantyExpiryDate.text.toString(),
                            amc_expiry_date = binding.tvAMCExpiryDate.text.toString(),
                            resolution_date = binding.tvResolutionDate.text.toString(),
                            resolved_by = binding.spinnerResolvedBy.selectedItem.toString(),
                            resolution_details = binding.etResolutionDetails.text.toString()
                        )

                    } catch (e: IOException) {
                        Log.e("my app1", e.message.toString())
                        Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                        return@launch
                    } catch (e: HttpException) {
                        Log.e("my app2", e.message.toString())
                        Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                        return@launch
                    } catch (e: Exception) {
                        Log.e("my app3", e.message.toString())
//                    Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    Log.d("my d", response.body().toString())
                    Log.d(
                        "my d",
                        "${binding.spinnerCustomer.selectedItem},${binding.tvIssueDate.text},${binding.etIssue.text}," +
                                "${binding.spinnerItemCode.selectedItem},${binding.spinnerWarrantyStatus.selectedItem}," +
                                "${binding.tvWarrantyExpiryDate.text},${binding.tvAMCExpiryDate.text},${binding.tvResolutionDate.text}," +
                                "${binding.spinnerResolvedBy.selectedItem},${binding.etResolutionDetails.text}")

                    if (response.isSuccessful && response.body() != null) {
                        withContext(Dispatchers.Main) {
                            val result = response.body()!!.data
                            Log.d("my app5", "request send successfully")
                            Toast.makeText(this@AddWarrantyClaimActivity, "Submitted successful!", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                }

            }

        }
    }

    private fun customer() {
        customerList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                CustomerRetrofitInstance.setBaseUrl(baseUrl)
                CustomerRetrofitInstance.api.getCustomer(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app11", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app12", result[0].name)
                    customerList = result

                    val temp = arrayListOf<String>()
                    customerList.forEach {
                        temp.add(it.name)
                    }

                    val customerAdapter = ArrayAdapter(
                        this@AddWarrantyClaimActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerCustomer.adapter = customerAdapter

                    binding.spinnerCustomer.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedCustomer = customerList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
    }

    private fun issueDate() {
        binding.tvIssueDate.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvIssueDate.text = selectedDate
                }, year, month, day
            ).show()
        }
    }

    private fun itemCode() {
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        itemCodeList = arrayListOf()
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                ItemCodeRetrofitInstance.setBaseUrl(baseUrl)
                ItemCodeRetrofitInstance.api.getItemCode(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app7", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app8", result[0].item_code)
                    itemCodeList = result as ArrayList<ItemCodeDataItem>

                    val temp = arrayListOf<String>()
                    itemCodeList.forEach {
                        temp.add("${it.name}\n(${it.item_name})")
                    }

                    val itemAdapter = ArrayAdapter(
                        this@AddWarrantyClaimActivity, android.R.layout.simple_spinner_item, temp
                    )

                    itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerItemCode.adapter = itemAdapter

                    binding.spinnerItemCode.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedItem = itemCodeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
    }

    private fun warrantyStatus(): String {

        val warrantyStatus = listOf("Under Warranty", "Out of Warranty", "Under AMC", "Out of AMC")
        val warrantyStatusAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, warrantyStatus)
        warrantyStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerWarrantyStatus.adapter = warrantyStatusAdapter

        binding.spinnerWarrantyStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedWarrantyStatus = warrantyStatus[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        return binding.spinnerWarrantyStatus.toString()

    }

    private fun warrantyExpiryDate() {
        binding.tvWarrantyExpiryDate.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvWarrantyExpiryDate.text = selectedDate
                }, year, month, day
            ).show()
        }
    }

    private fun awsExpiryDate() {
        binding.tvAMCExpiryDate.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvAMCExpiryDate.text = selectedDate
                }, year, month, day
            ).show()
        }
    }

    private fun resolutionDate() {
        binding.tvResolutionDate.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvResolutionDate.text = selectedDate
                }, year, month, day
            ).show()
        }
    }

    private fun resoledBy() {
        resolvedByList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                ResoledByRetrofitInstance.setBaseUrl(baseUrl)
                ResoledByRetrofitInstance.api.getResolvedBy(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app11", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app12", result[0].name)
                    resolvedByList = result

                    val temp = arrayListOf<String>()
                    resolvedByList.forEach {
                        temp.add(it.name)
                    }

                    val resolvedByAdapter = ArrayAdapter(
                        this@AddWarrantyClaimActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    resolvedByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerResolvedBy.adapter = resolvedByAdapter

                    binding.spinnerResolvedBy.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedResolvedBy = resolvedByList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
    }

}
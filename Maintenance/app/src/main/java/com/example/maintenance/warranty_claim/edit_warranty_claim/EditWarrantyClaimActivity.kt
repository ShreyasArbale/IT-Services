package com.example.maintenance.warranty_claim.edit_warranty_claim

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.maintenance.R
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.ActivityEditWarrantyClaimBinding
import com.example.maintenance.databinding.DialogConfirmDeleteWarrantyClaimBinding
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.resoledBy.ResoledByRetrofitInstance
import com.example.maintenance.resoledBy.model_reslovedBy.ResolvedByData
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.warranty_claim.WarrantyClaimRetrofitInstance
import com.example.maintenance.warranty_claim.edit_warranty_claim.model_edit_warranty.EditWarrantyClaim
import com.example.maintenance.customer.model_customer.CustomerDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.Calendar

class EditWarrantyClaimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditWarrantyClaimBinding
    private var id = ""
    private var mProgressDialog: Dialog? =null

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var itemCodeList: ArrayList<ItemCodeDataItem>
    private lateinit var resolvedByList: ArrayList<ResolvedByData>

    private lateinit var baseUrl : String

    private lateinit var selectedItem: ItemCodeDataItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityEditWarrantyClaimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        id = intent.getStringExtra("id").toString()

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        getData()
        putEditedData()
        deleteWarrantyClaim()

    }

    private fun editCustomer(response1: Response<EditWarrantyClaim?>): String{
        customerList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch (Dispatchers.IO){
            val response = try {
                CustomerRetrofitInstance.setBaseUrl(baseUrl)
                CustomerRetrofitInstance.api.getCustomer(token)
            }catch (e: IOException){
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null){
                hideProgressDialog()
                withContext(Dispatchers.Main){
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    customerList = result
                    Log.d("myapp", customerList.toString())

                    val temp = arrayListOf<String>()
                    customerList.forEach{
                        temp.add(it.name)
                    }

                    val customerAdapter = ArrayAdapter(this@EditWarrantyClaimActivity, android.R.layout.simple_spinner_item, temp)

                    customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerCustomer.adapter = customerAdapter

                    binding.spinnerCustomer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            val selectedCustomer = customerList[position]
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }

                    val oldCustomer = response1.body()!!.data.customer
                    var index = -1
                    Log.d("myapp", "$index  ${oldCustomer} $customerList")
                    customerList.forEachIndexed {position, data ->
                        if (data.name == oldCustomer){
                            index = position
                        }
                    }
                    binding.spinnerCustomer.setSelection(index)
                }
            }
        }

        return binding.spinnerCustomer.toString()
    }

    private fun editIssueDate() {
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

    private fun editItemCode(response1: Response<EditWarrantyClaim?>): String{
        itemCodeList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch (Dispatchers.IO){
            val response = try {
                ItemCodeRetrofitInstance.setBaseUrl(baseUrl)
                ItemCodeRetrofitInstance.api.getItemCode(token)
            }catch (e: IOException){
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null){
                hideProgressDialog()
                withContext(Dispatchers.Main){

                    val result = response.body()!!.data
                    Log.d("my app7", result[0].item_code)

                    itemCodeList = result as ArrayList<ItemCodeDataItem>
                    Log.d("myapp", itemCodeList.toString())

                    val temp = arrayListOf<String>()
                    itemCodeList.forEach{
                        temp.add("${it.name}\n(${it.item_name})")
                    }

                    val itemCodeAdapter = ArrayAdapter(this@EditWarrantyClaimActivity, android.R.layout.simple_spinner_item, temp)

                    itemCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerItemCode.adapter = itemCodeAdapter

                    binding.spinnerItemCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            selectedItem = itemCodeList[position]
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }

                    val oldItemCode = response1.body()!!.data.item_code
                    var index = -1
                    Log.d("myapp", "$index  ${oldItemCode} $itemCodeList")
                    itemCodeList.forEachIndexed {position, data ->
                        if (data.name == oldItemCode){
                            index = position
                        }
                    }
                    binding.spinnerItemCode.setSelection(index)
                    Log.d("myapp20", "$index  ${oldItemCode} ")
                }
            }
        }

        return binding.spinnerItemCode.toString()
    }

    private fun editWarrantyStatus(response: Response<EditWarrantyClaim?>): String{
        val warrantyStatusData = listOf("Under Warranty", "Out of Warranty", "Under AMC", "Out of AMC")

        val warrantyStatusAdapter = ArrayAdapter(this@EditWarrantyClaimActivity, android.R.layout.simple_spinner_item, warrantyStatusData)

        warrantyStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerWarrantyStatus.adapter = warrantyStatusAdapter

        binding.spinnerWarrantyStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedWarrantyStatus = warrantyStatusData[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val oldStatus=response.body()!!.data.warranty_amc_status
        val index = warrantyStatusData.indexOf(oldStatus)
        binding.spinnerWarrantyStatus.setSelection(index)
        return binding.spinnerWarrantyStatus.toString()
    }

    private fun editWarrantyExpiryDate() {
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

    private fun editAMCExpiryDate() {
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

    private fun editResolvedBy(response1: Response<EditWarrantyClaim?>): String{
        resolvedByList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch (Dispatchers.IO){
            val response = try {
                ResoledByRetrofitInstance.setBaseUrl(baseUrl)
                ResoledByRetrofitInstance.api.getResolvedBy(token)
            }catch (e: IOException){
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null){
                hideProgressDialog()
                withContext(Dispatchers.Main){
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    resolvedByList = result
                    Log.d("myapp", resolvedByList.toString())

                    val temp = arrayListOf<String>()
                    resolvedByList.forEach{
                        temp.add(it.name)
                    }

                    val resolvedByAdapter = ArrayAdapter(this@EditWarrantyClaimActivity, android.R.layout.simple_spinner_item, temp)

                    resolvedByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerResolvedBy.adapter = resolvedByAdapter

                    binding.spinnerResolvedBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            val selectedResolvedBy = resolvedByList[position]
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }

                    val oldResolvedBy = response1.body()!!.data.resolved_by
                    var index = -1
                    Log.d("myapp", "$index  ${oldResolvedBy} $resolvedByList")
                    resolvedByList.forEachIndexed {position, data ->
                        if (data.name == oldResolvedBy){
                            index = position
                        }
                    }
                    binding.spinnerResolvedBy.setSelection(index)
                }
            }
        }

        return binding.spinnerResolvedBy.toString()
    }

    private fun editResolutionDate() {
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

    private fun getData(){
        showCustomProgressDialog()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        WarrantyClaimRetrofitInstance.setBaseUrl(baseUrl)
        WarrantyClaimRetrofitInstance.api.getEditWarranty(token, id).enqueue(object :
            Callback<EditWarrantyClaim?> {
            override fun onResponse(call: Call<EditWarrantyClaim?>, response: Response<EditWarrantyClaim?>) {

                editCustomer(response)
                editIssueDate()
                editItemCode(response)
                editWarrantyStatus(response)
                editWarrantyExpiryDate()
                editAMCExpiryDate()
                editResolvedBy(response)
                editResolutionDate()

                binding.tvTitle.text = response.body()!!.data.name
                binding.tvIssueDate.text = response.body()!!.data.complaint_date
                binding.etIssue.setText(response.body()!!.data.complaint)
                binding.tvWarrantyExpiryDate.text = response.body()!!.data.warranty_expiry_date
                binding.tvAMCExpiryDate.text = response.body()!!.data.amc_expiry_date
                binding.tvResolutionDate.text = response.body()!!.data.resolution_date
                binding.etResolutionDetails.setText(response.body()!!.data.resolution_details)
            }

            override fun onFailure(call: Call<EditWarrantyClaim?>, t: Throwable) {
                Toast.makeText(this@EditWarrantyClaimActivity, "${t.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun putEditedData(){
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        binding.buttonSubmit.setOnClickListener {

            if (binding.etIssue.text!!.isEmpty()){
                Toast.makeText(this, "Enter Subject", Toast.LENGTH_SHORT).show()
            }

            else if (binding.etResolutionDetails.text!!.isEmpty()){
                Toast.makeText(this, "Enter Resolution Details", Toast.LENGTH_SHORT).show()
            }

            else{

                GlobalScope.launch(Dispatchers.IO) {
                    val response = try {
                        WarrantyClaimRetrofitInstance.setBaseUrl(baseUrl)
                        WarrantyClaimRetrofitInstance.api.editWarrantyClaim(
                            token,
                            id,
                            customer = binding.spinnerCustomer.selectedItem.toString(),
                            complaint_date = binding.tvIssueDate.text.toString(),
                            complaint = binding.etIssue.text.toString(),
                            item_code = selectedItem.name,
                            warranty_amc_status = binding.spinnerWarrantyStatus.selectedItem.toString(),
                            warranty_expiry_date = binding.tvWarrantyExpiryDate.text.toString(),
                            amc_expiry_date = binding.tvAMCExpiryDate.text.toString(),
                            resolution_date = binding.tvResolutionDate.text.toString(),
                            resolved_by = binding.spinnerResolvedBy.selectedItem.toString(),
                            resolution_details = binding.etResolutionDetails.text.toString(),

                            )

                    } catch (e: IOException) {
                        Log.e("my app1", e.message.toString())
                        Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                        return@launch
                    } catch (e: HttpException) {
                        Log.e("my app2", e.message.toString())
                        Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    catch (e: Exception) {
                        Log.e("my app3", e.message.toString())
//                    Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    Log.d("my d", response.toString())
//                Log.d("my app", response.headers().toString())
                    if (response.isSuccessful && response.body() != null) {
                        hideProgressDialog()
                        withContext(Dispatchers.Main) {
                            val result = response.body()!!.data
                            Log.d("my app4", result.status)
                            Log.d("my app5", "request send successfully")
                            Toast.makeText(this@EditWarrantyClaimActivity, "Edited Successfully", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                }


            }
        }
    }

    private fun deleteWarrantyClaim(){
        binding.buttonDelete.setOnClickListener {

            showCustomDialogBox()

        }
    }

    private fun showCustomDialogBox(){
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmDeleteWarrantyClaimBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener(){
            this@EditWarrantyClaimActivity.finish()
            GlobalScope.launch {
                val response = try {
                    WarrantyClaimRetrofitInstance.setBaseUrl(baseUrl)
                    WarrantyClaimRetrofitInstance.api.deleteWarrantyClaim(token, id)
                } catch (e: Exception){
                    return@launch
                }
                if(response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditWarrantyClaimActivity, "Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener(){
            customDialog.dismiss()
        }
        customDialog.show()
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
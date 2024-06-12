package com.example.maintenance.issue.add_issue

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.R
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.ActivityAddIssueBinding
import com.example.maintenance.issue.IssueRetrofitInstance
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.customer.model_customer.CustomerDataItem
import com.example.maintenance.databinding.DialogAddItemIssueBinding
import com.example.maintenance.databinding.DialogAddItemMaintenanceBinding
import com.example.maintenance.databinding.DialogShowSignatureBinding
import com.example.maintenance.department.DepartmentRetrofitInstance
import com.example.maintenance.department.model_department.DepartmentItemData
import com.example.maintenance.issue.add_issue.model_add_issue.CustomMaterialUsed
import com.example.maintenance.issue.model_issue.IssueItem
import com.example.maintenance.issue.model_item_name.ItemNameData
import com.example.maintenance.issue.model_item_name.ItemNameList
import com.example.maintenance.issue_type.IssueTypeRetrofitInstance
import com.example.maintenance.issue_type.model_issue_type.IssueTypeItemData
import com.example.maintenance.location.LocationRetrofitInstance
import com.example.maintenance.location.model_location.LocationData
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.office.OfficeRetrofitInstance
import com.example.maintenance.office.model_office.OfficeData
import com.example.maintenance.user.UserRetrofitInstance
import com.example.maintenance.user.model_user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AddIssueActivity : AppCompatActivity() {

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var departmentList: ArrayList<DepartmentItemData>
    private lateinit var issueType: ArrayList<IssueTypeItemData>
    private lateinit var locationList: ArrayList<LocationData>
    private lateinit var officeList: ArrayList<OfficeData>
    private lateinit var userList: ArrayList<UserData>

    private lateinit var itemList: ArrayList<ItemCodeDataItem>
    private lateinit var selectedItem: ItemCodeDataItem
    private lateinit var itemNameList: ItemNameData
    private var newItemList: ArrayList<ItemNameList> = arrayListOf()
    private lateinit var rvAdapter: ItemNameAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null

    private lateinit var requestModel: com.example.maintenance.issue.add_issue.model_add_issue.IssueItem

    private lateinit var binding: ActivityAddIssueBinding
    private var customDialog: Dialog? = null
    private lateinit var dialogBinding: DialogAddItemIssueBinding
    private lateinit var dialogSignBinding: DialogShowSignatureBinding
    private var mProgressDialog: Dialog? = null
    private lateinit var signBitmap : Bitmap

    private lateinit var baseUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddIssueBinding.inflate(layoutInflater)

        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        requestModel = com.example.maintenance.issue.add_issue.model_add_issue.IssueItem(
            data = com.example.maintenance.issue.add_issue.model_add_issue.Data(
                "",
                "",
                "",
                "",
                "",
                "",
                custom_material_used = arrayListOf(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
            )
        )

        department()
        status()
        customer()
        priority()
        location()
        issueType()
        office()
        user()
        signatureStatus()

        binding.btnAddSign.setOnClickListener {
            sign()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.btnAddItem.setOnClickListener {
            dialog(baseUrl, token)
            customDialog?.show()
        }

        layoutManager = LinearLayoutManager(this)
        binding.rvAddItem.layoutManager = layoutManager
        rvAdapter = ItemNameAdapter(itemNameList = arrayListOf())
        binding.rvAddItem.adapter = rvAdapter

        binding.buttonSubmit.setOnClickListener {
            submit(token)
        }

    }

    private fun submit(token: String) {

        val list =
            arrayListOf<CustomMaterialUsed>()

        if (binding.etSubject.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Subject", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etContactPerson.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Contact Person", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etPhone.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etPhone.length() != 10) {
            Toast.makeText(this@AddIssueActivity, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etEmail.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
            Toast.makeText(this@AddIssueActivity, "Please enter a valid Email", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etActionTaken.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Action Taken", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etCustomerRemark.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Customer Remark", Toast.LENGTH_SHORT).show()
        }
        else if(newItemList.isEmpty()){
            Toast.makeText(this, "Add one item at least", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etDescription.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        }
        else {

            GlobalScope.launch(Dispatchers.IO) {
                val response = try {

                    newItemList.forEach {
                        list.add(
                            CustomMaterialUsed(
                                it.name,
                                it.itemCode,
                                it.itemName
                            )
                        )
                    }

                    requestModel.data.custom_material_used = list
                    requestModel.data.customer = binding.spinnerCustomer.selectedItem.toString()
                    requestModel.data.subject = binding.etSubject.text.toString()
                    requestModel.data.custom_department = binding.spinnerDepartment.selectedItem.toString()
                    requestModel.data.custom_mob_number = binding.etPhone.text.toString()
                    requestModel.data.custom_location = binding.spinnerLocation.selectedItem.toString()
                    requestModel.data.custom_office =binding.spinnerOffice.selectedItem.toString()
                    requestModel.data.custom_contact_person = binding.etContactPerson.text.toString()
                    requestModel.data.custom_assign_user = binding.spinnerUser.selectedItem.toString()
                    requestModel.data.action_taken = binding.etActionTaken.text.toString()
                    requestModel.data.customer_remarks = binding.etCustomerRemark.text.toString()
                    requestModel.data.custom_customer_signature = binding.spinnerSignatureStatus.selectedItem.toString()
                    requestModel.data.raised_by = binding.etEmail.text.toString()
                    requestModel.data.status = binding.spinnerStatus.selectedItem.toString()
                    requestModel.data.priority = binding.spinnerPriority.selectedItem.toString()
                    requestModel.data.issue_type = binding.spinnerIssueType.selectedItem.toString()
                    requestModel.data.description = binding.etDescription.text.toString()
                    requestModel.data.resolution_details = binding.etResolutionDetails.text.toString()


                    IssueRetrofitInstance.setBaseUrl(baseUrl)
                    IssueRetrofitInstance.api.createIssue(token, requestModel)
//                        "",
//                        subject = binding.etSubject.text.toString(),
//                        customer = binding.spinnerCustomer.selectedItem.toString(),
//                        custom_department = binding.spinnerDepartment.selectedItem.toString(),
//                        custom_mob_number = binding.etPhone.text.toString(),
//                        custom_location = binding.spinnerLocation.selectedItem.toString(),
//                        custom_office =binding.spinnerOffice.selectedItem.toString(),
//                        custom_contact_person = binding.etContactPerson.text.toString(),
//                        custom_assign_user = binding.spinnerUser.selectedItem.toString(),
//                        action_taken = binding.etActionTaken.text.toString(),
//                        customer_remarks = binding.etCustomerRemark.text.toString(),
//                        custom_customer_signature = binding.spinnerSignatureStatus.selectedItem.toString(),
//                        raised_by = binding.etEmail.text.toString(),
//                        status = binding.spinnerStatus.selectedItem.toString(),
//                        priority = binding.spinnerPriority.selectedItem.toString(),
//                        issue_type = binding.spinnerIssueType.selectedItem.toString(),
//                        description = binding.etDescription.text.toString(),
//                        resolution_details = binding.etResolutionDetails.text.toString()


                } catch (e: IOException) {
                    Log.e("my app1", e.message.toString())
                    return@launch
                } catch (e: HttpException) {
                    Log.e("my app2", e.message.toString())
                    return@launch
                } catch (e: Exception) {
                    Log.e("my app3", e.message.toString())
                    return@launch
                }
                Log.d("my d", response.body().toString())
                Log.d("my d", response.message())
                Log.d("my d", response.toString())
                Log.d("my d", list.toString())

                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val result = response.body()!!.data

                        Log.d("my app5", "request send successfully")
                        Toast.makeText(
                            this@AddIssueActivity,
                            "Submitted Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                } else {
                    Log.d("my save fail", response.message())
                }
            }
        }
    }

    private fun dialog(baseUrl: String, token: String) {
        customDialog = Dialog(this)
        dialogBinding = DialogAddItemIssueBinding.inflate(layoutInflater)
        customDialog!!.setContentView(dialogBinding.root)
        customDialog!!.setCanceledOnTouchOutside(false)

        dialogBinding.imageButtonCancel.setOnClickListener {
            customDialog!!.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            customDialog!!.dismiss()
        }

        itemCode(baseUrl, token)

        dialogBinding.btnSave.setOnClickListener {

            val newElement = ItemNameList(
                name = selectedItem.name,
                itemCode = selectedItem.name,
                itemName = dialogBinding.tvItemName.text.toString()
            )
            newItemList.add(newElement)
            rvAdapter.addItem(
                newElement
            )
            customDialog!!.dismiss()

        }

    }

    private fun department(): String {
        departmentList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                DepartmentRetrofitInstance.setBaseUrl(baseUrl)
                DepartmentRetrofitInstance.api.getDepartment(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    departmentList = result

                    val temp = arrayListOf<String>()
                    departmentList.forEach {
                        temp.add(it.name)
                    }

                    val departmentAdapter = ArrayAdapter(
                        this@AddIssueActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerDepartment.adapter = departmentAdapter

                    binding.spinnerDepartment.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedDepartment = departmentList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
        return binding.spinnerDepartment.toString()
    }

    private fun status(): String {
        val statusData = listOf(
            "Open",
            "Replied",
            "On Hold",
            "Resolved",
            "Closed",
            "Re-Open",
            "Explain to Customer"
        )

        var statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusData)

        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerStatus.adapter = statusAdapter

        binding.spinnerStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    var selectedStatus = statusData[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        return binding.spinnerStatus.toString()
    }

    private fun customer(): String {
        customerList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        Log.d("token", token)
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
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    customerList = result

                    val temp = arrayListOf<String>()
                    customerList.forEach {
                        temp.add(it.name)
                    }

                    val customerAdapter = ArrayAdapter(
                        this@AddIssueActivity,
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
        return binding.spinnerCustomer.toString()
    }

    private fun priority(): String {
        val priorityData = listOf("High", "Low", "Medium")

        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityData)

        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerPriority.adapter = priorityAdapter

        binding.spinnerPriority.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedPriority = priorityData[position]

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        return binding.spinnerPriority.toString()
    }

    private fun location(): String {
        locationList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                LocationRetrofitInstance.setBaseUrl(baseUrl)
                LocationRetrofitInstance.api.getLocation(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    locationList = result

                    val temp = arrayListOf<String>()
                    locationList.forEach {
                        temp.add(it.name)
                    }

                    val locationAdapter = ArrayAdapter(
                        this@AddIssueActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerLocation.adapter = locationAdapter

                    binding.spinnerLocation.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedLocation = locationList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
        return binding.spinnerLocation.toString()
    }

    private fun issueType(): String {
        issueType = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                IssueTypeRetrofitInstance.setBaseUrl(baseUrl)
                IssueTypeRetrofitInstance.api.getIssueType(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    issueType = result

                    val temp = arrayListOf<String>()
                    issueType.forEach {
                        temp.add(it.name)
                    }

                    val issueTypeAdapter = ArrayAdapter(
                        this@AddIssueActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    issueTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerIssueType.adapter = issueTypeAdapter

                    binding.spinnerIssueType.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedIssueType = issueType[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
        return binding.spinnerIssueType.toString()
    }

    private fun office(): String {
        officeList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                OfficeRetrofitInstance.setBaseUrl(baseUrl)
                OfficeRetrofitInstance.api.getOffice(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    officeList = result

                    val temp = arrayListOf<String>()
                    officeList.forEach {
                        temp.add(it.name)
                    }

                    val officeAdapter = ArrayAdapter(
                        this@AddIssueActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerOffice.adapter = officeAdapter

                    binding.spinnerOffice.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedOffice = officeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
        return binding.spinnerOffice.toString()
    }

    private fun user(): String {
        userList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                UserRetrofitInstance.setBaseUrl(baseUrl)
                UserRetrofitInstance.api.getUser(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    userList = result

                    val temp = arrayListOf<String>()
                    userList.forEach {
                        temp.add(it.name)
                    }

                    val userAdapter = ArrayAdapter(
                        this@AddIssueActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerUser.adapter = userAdapter

                    binding.spinnerUser.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedUser = userList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
        return binding.spinnerUser.toString()
    }

    private fun signatureStatus(): String {
        val signatureData = listOf("No", "Yes")

        val signatureAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, signatureData)

        signatureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerSignatureStatus.adapter = signatureAdapter

        binding.spinnerSignatureStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedSignature = signatureData[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        return binding.spinnerSignatureStatus.toString()
    }

    private fun sign(){
        customDialog = Dialog(this)
        dialogSignBinding = DialogShowSignatureBinding.inflate(layoutInflater)
        customDialog!!.setContentView(dialogSignBinding.root)
        customDialog!!.setCanceledOnTouchOutside(false)
        customDialog?.show()

        dialogSignBinding.btnReload.setOnClickListener {
            dialogSignBinding.signatureView.clearCanvas()
        }

        dialogSignBinding.btnNo.setOnClickListener {
            customDialog?.dismiss()
        }

        dialogSignBinding.btnYes.setOnClickListener {
            signBitmap = dialogSignBinding.signatureView.signatureBitmap

            if (signBitmap != null){
                binding.imgSign.setImageBitmap(signBitmap)
            }
            customDialog?.dismiss()
        }
    }

    private fun itemCode(baseUrl: String, token: String) {
        itemList = arrayListOf()
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                ItemCodeRetrofitInstance.setBaseUrl(baseUrl)
                ItemCodeRetrofitInstance.api.getItemCode(token)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: Exception) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {

                    val result = response.body()!!.data
                    itemList = result as ArrayList<ItemCodeDataItem>

                    val temp = arrayListOf<String>()
                    itemList.forEach {
                        temp.add("${it.name}")
                    }


                    val itemAdapter = ArrayAdapter(
                        this@AddIssueActivity, android.R.layout.simple_spinner_item, temp
                    )

                    itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    dialogBinding.spinnerItemCode.adapter = itemAdapter

                    dialogBinding.spinnerItemCode.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedItem = itemList[position]
                                showItemName(token, selectedItem.name)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }
    }

    private fun showItemName(token: String, name: String) {
        showCustomProgressDialog()

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                intent.putExtra("item", name)

                IssueRetrofitInstance.setBaseUrl(baseUrl)
                IssueRetrofitInstance.api.getItemName(token, name)
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
                    itemNameList = result
                    dialogBinding.tvItemName.setText(response.body()!!.data.item_name).toString()

                }
            } else {
                Toast.makeText(this@AddIssueActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
//                hideProgressDialog()
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
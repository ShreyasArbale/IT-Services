package com.example.maintenance.issue.edit_issue

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.R
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.ActivityEditIssueBinding
import com.example.maintenance.databinding.DialogConfirmDeleteIssueBinding
import com.example.maintenance.issue.IssueRetrofitInstance
import com.example.maintenance.issue.edit_issue.model_edit_issue.Edit
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.customer.model_customer.CustomerDataItem
import com.example.maintenance.databinding.HorizontalRecyclerItemNameBinding
import com.example.maintenance.department.DepartmentRetrofitInstance
import com.example.maintenance.department.model_department.DepartmentItemData
import com.example.maintenance.issue.edit_issue.model_edit_issue.CustomMaterialUsed
import com.example.maintenance.issue_type.IssueTypeRetrofitInstance
import com.example.maintenance.issue_type.model_issue_type.IssueTypeItemData
import com.example.maintenance.location.LocationRetrofitInstance
import com.example.maintenance.location.model_location.LocationData
import com.example.maintenance.maintenance.MaintenanceRetrofitInstance
import com.example.maintenance.office.OfficeRetrofitInstance
import com.example.maintenance.office.model_office.OfficeData
import com.example.maintenance.user.UserRetrofitInstance
import com.example.maintenance.user.model_user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class EditIssueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditIssueBinding
    private lateinit var bindingHorizontal: HorizontalRecyclerItemNameBinding
    private lateinit var token: String
    private lateinit var baseUrl: String

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var departmentList: ArrayList<DepartmentItemData>
    private lateinit var issueType: ArrayList<IssueTypeItemData>
    private lateinit var locationList: ArrayList<LocationData>
    private lateinit var officeList: ArrayList<OfficeData>
    private lateinit var userList: ArrayList<UserData>

    private lateinit var issueItemList: ArrayList<CustomMaterialUsed>
    private lateinit var rvAdapter: EditIssueItemAdapter
    private lateinit var rvEditAdapter: EditIssueItemListAdapter

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var newEditItemList: ArrayList<EditIssueItemList> = arrayListOf()
    private lateinit var requestModel: com.example.maintenance.issue.add_issue.model_add_issue.IssueItem

    private var id = ""
    private var mProgressDialog: Dialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditIssueBinding.inflate(layoutInflater)

        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        token = SharedPreferenceManager.getInstance(applicationContext).getToken
        issueItemList = arrayListOf()
        id = intent.getStringExtra("id").toString()
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

        val list =
            arrayListOf<com.example.maintenance.issue.add_issue.model_add_issue.CustomMaterialUsed>()

        getData(token)

        binding.buttonDelete.setOnClickListener {
            deleteIssueDialogBox(token)
        }

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            submit(token, baseUrl, list)
        }

        layoutManager = LinearLayoutManager(this)
        binding.rvEditItem.layoutManager = layoutManager
        rvEditAdapter = EditIssueItemListAdapter(itemList = arrayListOf())
        binding.rvEditItem.adapter = rvEditAdapter
    }

    private fun getData(token: String) {
        showCustomProgressDialog()

        IssueRetrofitInstance.setBaseUrl(baseUrl)
        IssueRetrofitInstance.api.getEditIssue(token, id).enqueue(object :
            Callback<Edit?> {
            override fun onResponse(call: Call<Edit?>, response: Response<Edit?>) {

                editStatus(response)
                editCustomer(response, token)
                editDepartment(response, token)
                editPriority(response)
                editIssueType(response, token)
                editLocation(response, token)
                editOffice(response, token)
                editUser(response, token)
                editSignatureStatus(response)

                binding.tvTitle.text = response.body()!!.data.name
                binding.etSubject.setText(response.body()!!.data.subject)
                binding.etPhone.setText(response.body()!!.data.custom_mob_number)
                binding.etContactPerson.setText(response.body()!!.data.custom_contact_person)
                binding.etEmail.setText(response.body()!!.data.raised_by)
                binding.etActionTaken.setText(response.body()!!.data.action_taken)
                binding.etCustomerRemark.setText(response.body()!!.data.customer_remarks)
                binding.etDescription.setText(response.body()!!.data.description)
                binding.etResolutionDetails.setText(response.body()!!.data.resolution_details)

                getItem(token)
            }

            override fun onFailure(call: Call<Edit?>, t: Throwable) {
                Toast.makeText(this@EditIssueActivity, "${t.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getItem(token: String) {
        super.onResume()

        GlobalScope.launch(Dispatchers.IO) {

            val response = try {
                IssueRetrofitInstance.setBaseUrl(baseUrl)
                IssueRetrofitInstance.api.getIssueItem(token, id)
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
                    val result = response.body()!!.data.custom_material_used

//                    Log.d("my app", result[0].name)
                    issueItemList = result
                    binding.rvEditItem.apply {
                        rvAdapter = EditIssueItemAdapter(
                            issueItemList
//                            , itemListListener = IssueItemListener
                        )
                        adapter = rvAdapter
                        layoutManager = LinearLayoutManager(this@EditIssueActivity)
                    }
                }
            }
            Log.d("myapp", "onResume called")
        }
    }

    private fun submit(token: String, baseUrl: String, list: ArrayList<com.example.maintenance.issue.add_issue.model_add_issue.CustomMaterialUsed>) {

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
            Toast.makeText(this@EditIssueActivity, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etEmail.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
            Toast.makeText(this@EditIssueActivity, "Please enter a valid Email", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etActionTaken.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Action Taken", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etCustomerRemark.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Customer Remark", Toast.LENGTH_SHORT).show()
        }
        else if (binding.etDescription.text!!.isEmpty()) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        }
        else {

            GlobalScope.launch(Dispatchers.IO) {

                val response = try {

                    newEditItemList.forEach {
                        list.add(
                            com.example.maintenance.issue.add_issue.model_add_issue.CustomMaterialUsed(
                                it.itemCode,
                                it.itemCode,
                                it.itemName
                            )
                        )
                    }

                    requestModel.data.custom_material_used = list
                    requestModel.data.customer = binding.spinnerCustomer.selectedItem.toString()
                    requestModel.data.subject = binding.etSubject.text.toString()
                    requestModel.data.custom_department =
                        binding.spinnerDepartment.selectedItem.toString()
                    requestModel.data.custom_mob_number = binding.etPhone.text.toString()
                    requestModel.data.custom_location =
                        binding.spinnerLocation.selectedItem.toString()
                    requestModel.data.custom_office = binding.spinnerOffice.selectedItem.toString()
                    requestModel.data.custom_contact_person =
                        binding.etContactPerson.text.toString()
                    requestModel.data.custom_assign_user =
                        binding.spinnerUser.selectedItem.toString()
                    requestModel.data.action_taken = binding.etActionTaken.text.toString()
                    requestModel.data.customer_remarks = binding.etCustomerRemark.text.toString()
                    requestModel.data.custom_customer_signature =
                        binding.spinnerSignatureStatus.selectedItem.toString()
                    requestModel.data.raised_by = binding.etEmail.text.toString()
                    requestModel.data.status = binding.spinnerStatus.selectedItem.toString()
                    requestModel.data.priority = binding.spinnerPriority.selectedItem.toString()
                    requestModel.data.issue_type = binding.spinnerIssueType.selectedItem.toString()
                    requestModel.data.description = binding.etDescription.text.toString()
                    requestModel.data.resolution_details =
                        binding.etResolutionDetails.text.toString()

                    IssueRetrofitInstance.setBaseUrl(baseUrl)

                    IssueRetrofitInstance.api.editIssue(
                        token,
                        id,
                        requestModel

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

                Log.d("my app4", response.raw().message())
                Log.d("my app5", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val result = response.body()!!.data
                        Log.d("my app66", "${response.body()} request send successfully")
                        Toast.makeText(
                            this@EditIssueActivity,
                            "Saved Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }
            }
        }
    }

    private fun editStatus(response: Response<Edit?>): String {
        val statusData = listOf("Open", "Replied", "On Hold", "Resolved", "Closed")

        var statusAdapter =
            ArrayAdapter(this@EditIssueActivity, android.R.layout.simple_spinner_item, statusData)

        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerStatus.adapter = statusAdapter

        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        val oldStatus = response.body()!!.data.status
        val index = statusData.indexOf(oldStatus)
        binding.spinnerStatus.setSelection(index)
        return binding.spinnerStatus.toString()
    }

    private fun editCustomer(response1: Response<Edit?>, token: String): String {

        customerList = arrayListOf()

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
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    customerList = result
                    Log.d("myapp", customerList.toString())

                    val temp = arrayListOf<String>()
                    customerList.forEach {
                        temp.add(it.name)
                    }

                    val customerAdapter = ArrayAdapter(
                        this@EditIssueActivity,
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

                    val oldCustomer = response1.body()!!.data.customer
                    var index = -1
                    Log.d("myapp", "$index  ${oldCustomer} $customerList")
                    customerList.forEachIndexed { position, data ->
                        if (data.name == oldCustomer) {
                            index = position
                        }
                    }
                    binding.spinnerCustomer.setSelection(index)
                }
            }
        }

        return binding.spinnerCustomer.toString()
    }

    private fun editOffice(response1: Response<Edit?>, token: String): String {
        officeList = arrayListOf()

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
                        this@EditIssueActivity,
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

                    val oldOffice = response1.body()!!.data.custom_office
                    var index = -1
                    Log.d("myapp", "$index  ${oldOffice} $officeList")
                    officeList.forEachIndexed { position, data ->
                        if (data.name == oldOffice) {
                            index = position
                        }
                    }
                    binding.spinnerOffice.setSelection(index)
                }
            }
        }
        return binding.spinnerOffice.toString()
    }

    private fun editLocation(response1: Response<Edit?>, token: String): String {

        locationList = arrayListOf()

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
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    locationList = result
                    Log.d("myapp", locationList.toString())

                    val temp = arrayListOf<String>()
                    locationList.forEach {
                        temp.add(it.name)
                    }

                    val locationAdapter = ArrayAdapter(
                        this@EditIssueActivity,
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

                    val oldLocation = response1.body()!!.data.custom_location
                    var index = -1
                    Log.d("myapp", "$index  ${oldLocation} $locationList")
                    locationList.forEachIndexed { position, data ->
                        if (data.name == oldLocation) {
                            index = position
                        }
                    }
                    binding.spinnerLocation.setSelection(index)
                }
            }
        }

        return binding.spinnerLocation.toString()
    }

    private fun editDepartment(response1: Response<Edit?>, token: String): String {

        departmentList = arrayListOf()

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
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    departmentList = result
                    Log.d("myapp", departmentList.toString())

                    val temp = arrayListOf<String>()
                    departmentList.forEach {
                        temp.add(it.name)
                    }

                    val departmentAdapter = ArrayAdapter(
                        this@EditIssueActivity,
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

                    val oldDepartment = response1.body()!!.data.custom_department
                    var index = -1
                    Log.d("myapp", "$index  ${oldDepartment} $departmentList")
                    departmentList.forEachIndexed { position, data ->
                        if (data.name == oldDepartment) {
                            index = position
                        }
                    }
                    binding.spinnerDepartment.setSelection(index)
                }
            }
        }

        return binding.spinnerDepartment.toString()
    }

    private fun editPriority(response: Response<Edit?>): String {
        val priorityData = listOf("High", "Low", "Medium")

        val priorityAdapter =
            ArrayAdapter(this@EditIssueActivity, android.R.layout.simple_spinner_item, priorityData)

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

        val oldStatus = response.body()!!.data.priority
        val index = priorityData.indexOf(oldStatus)
        binding.spinnerPriority.setSelection(index)
        return binding.spinnerPriority.toString()
    }

    private fun editSignatureStatus(response: Response<Edit?>): String {
        val signatureData = listOf("Yes", "No")

        val signatureAdapter = ArrayAdapter(
            this@EditIssueActivity,
            android.R.layout.simple_spinner_item,
            signatureData
        )

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

        val oldSignature = response.body()!!.data.custom_customer_signature
        val index = signatureData.indexOf(oldSignature)
        binding.spinnerSignatureStatus.setSelection(index)
        return binding.spinnerSignatureStatus.toString()
    }

    private fun editIssueType(response1: Response<Edit?>, token: String): String {

        issueType = arrayListOf()

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
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    issueType = result
                    Log.d("myapp", issueType.toString())

                    val temp = arrayListOf<String>()
                    issueType.forEach {
                        temp.add(it.name)
                    }

                    val issueTypeAdapter = ArrayAdapter(
                        this@EditIssueActivity,
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

                    val oldIssueType = response1.body()!!.data.issue_type
                    var index = -1
                    Log.d("myapp", "$index  ${oldIssueType} $issueType")
                    issueType.forEachIndexed { position, data ->
                        if (data.name == oldIssueType) {
                            index = position
                        }
                    }
                    binding.spinnerIssueType.setSelection(index)
                }
            }
        }

        return binding.spinnerIssueType.toString()
    }

    private fun editUser(response1: Response<Edit?>, token: String): String {

        userList = arrayListOf()

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
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    userList = result
                    Log.d("myapp", userList.toString())

                    val temp = arrayListOf<String>()
                    userList.forEach {
                        temp.add(it.name)
                    }

                    val userAdapter = ArrayAdapter(
                        this@EditIssueActivity,
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

                    val oldUser = response1.body()!!.data.custom_assign_user
                    var index = -1
                    Log.d("myapp", "$index  ${oldUser} $userList")
                    userList.forEachIndexed { position, data ->
                        if (data.name == oldUser) {
                            index = position
                        }
                    }
                    binding.spinnerUser.setSelection(index)
                }
            }
        }

        return binding.spinnerUser.toString()
    }

    private fun deleteIssueDialogBox(token: String) {

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmDeleteIssueBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener() {
            this@EditIssueActivity.finish()
            GlobalScope.launch {
                val response = try {
                    IssueRetrofitInstance.setBaseUrl(baseUrl)
                    IssueRetrofitInstance.api.deleteIssue(token, id)
                } catch (e: Exception) {
                    return@launch
                }
                if (response.isSuccessful) {
                    hideProgressDialog()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditIssueActivity,
                            "Issue deleted successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@EditIssueActivity, "Deleted successful", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener() {
            customDialog.dismiss()
        }
        customDialog.show()
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
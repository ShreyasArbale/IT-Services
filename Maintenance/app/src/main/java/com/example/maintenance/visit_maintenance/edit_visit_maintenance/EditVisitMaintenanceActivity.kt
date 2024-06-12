package com.example.maintenance.visit_maintenance.edit_visit_maintenance

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.R
import com.example.maintenance.SalesPerson.SalesPersonRetrofitInstance
import com.example.maintenance.SalesPerson.model_sales_person.SalesPersonDataItem
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.ActivityEditVisitMaintenanceBinding
import com.example.maintenance.databinding.DialogConfirmDeleteVisitMaintenanceBinding
import com.example.maintenance.databinding.DialogConfirmItemDeleteMaintenanceBinding
import com.example.maintenance.databinding.DialogEditItemVisitMaintenanceBinding
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem

import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.visit_maintenance.VisitMaintenanceInstance
import com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.AddVisitMaintenanceModel
import com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.Data
import com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_edit_visit_maintenance.EditVisitMaintenance
import com.example.maintenance.visit_maintenance.edit_visit_maintenance.model_edit_visit_maintenance.Purpose
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

class EditVisitMaintenanceActivity : AppCompatActivity(), VisitItemListener {
    private lateinit var binding: ActivityEditVisitMaintenanceBinding
    private var id = ""
    private var mProgressDialog: Dialog? = null

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var itemCodeList: ArrayList<ItemCodeDataItem>
    private lateinit var salesPersonList: ArrayList<SalesPersonDataItem>
    private lateinit var maintenanceVisitItemList: ArrayList<Purpose>
    private lateinit var newEditVisitItemList: ArrayList<EditVisitItemList>

    private lateinit var rvEditAdapter: EditVisitItemListAdapter
    private lateinit var rvAdapter: EditVisitMaintenanceAdapter

    private lateinit var requestModel: AddVisitMaintenanceModel

    private var layoutManager: RecyclerView.LayoutManager? = null

    private lateinit var  baseUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVisitMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        id = intent.getStringExtra("id").toString()
        maintenanceVisitItemList = arrayListOf()
        newEditVisitItemList = arrayListOf()
        requestModel = AddVisitMaintenanceModel(data = Data("", "",  "","", arrayListOf()))

        getData()

        binding.buttonSubmit.setOnClickListener {
            submit()
        }

        binding.buttonDelete.setOnClickListener {
            showCustomDialogBox()
        }

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        layoutManager = LinearLayoutManager(this)
        binding.rvAddVisitMaintenance.layoutManager = layoutManager
        rvEditAdapter = EditVisitItemListAdapter(itemList = arrayListOf())
        binding.rvAddVisitMaintenance.adapter = rvEditAdapter

    }

    private fun getData() {
        showCustomProgressDialog()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        VisitMaintenanceInstance.setBaseUrl(baseUrl)
        VisitMaintenanceInstance.api.getEditVisitMaintenance(token, id).enqueue(object :
            Callback<EditVisitMaintenance?> {

            override fun onResponse(
                call: Call<EditVisitMaintenance?>,
                response: Response<EditVisitMaintenance?>
            ) {
                editCustomer(response)
                completionStatus(response)
                maintenanceType(response)
                maintenanceDate()

                binding.tvTitle.text = response.body()!!.data.name
                binding.tvMaintenanceDate.text = response.body()!!.data.mntc_date

                getItem()
            }

            override fun onFailure(call: Call<EditVisitMaintenance?>, t: Throwable) {
                Toast.makeText(
                    this@EditVisitMaintenanceActivity,
                    "${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getItem() {
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                VisitMaintenanceInstance.setBaseUrl(baseUrl)
                VisitMaintenanceInstance.api.getItemVisitMaintenance(token, id)
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
                    val result = response.body()!!.data.purposes

//                    Log.d("my app", result[0].name)
                    maintenanceVisitItemList = result
                    binding.rvAddVisitMaintenance.apply {
                        rvAdapter = EditVisitMaintenanceAdapter(
                            maintenanceVisitItemList,
                            this@EditVisitMaintenanceActivity
                        )
                        adapter = rvAdapter
                        layoutManager = LinearLayoutManager(this@EditVisitMaintenanceActivity)
                        (layoutManager as LinearLayoutManager).reverseLayout = true
                        (layoutManager as LinearLayoutManager).stackFromEnd = true
                    }
                }
            }
            Log.d("myapp", "onResume called")
        }
    }

    override fun onVisitItemClick(
        name: String,
        itemCode: String,
        servicePerson: String,
        description: String,
        workDone: String
    ) {
        intent.putExtra("id", name)
        dialogItemShow(
            name,
            itemCode,
            servicePerson,
            description,
            workDone
        )
    }

    override fun onVisitMaintenanceDeleteItemClick(name: String) {
        intent.putExtra("id", name)
        deleteVisitMaintenanceItem(name)
    }

    private fun deleteVisitMaintenanceItem(name: String) {
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmItemDeleteMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener {
            showCustomProgressDialog()
            this@EditVisitMaintenanceActivity
            GlobalScope.launch {
                val response = try {
                    VisitMaintenanceInstance.setBaseUrl(baseUrl)
                    VisitMaintenanceInstance.api.deleteVisitMaintenance(token, id)
                } catch (e: Exception) {
                    Log.e("my yes", e.message.toString())
                    return@launch
                }
                Log.d("my yes2", response.message().toString())
                if (response.isSuccessful) {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditVisitMaintenanceActivity,
                            "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        customDialog.dismiss()
                    }
                }
            }
            finish()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun dialogItemShow(
        name: String,
        itemCode: String,
        salesPerson: String,
        description: String,
        workDone: String
    ) {
        super.onResume()

        val customDialog = Dialog(this)
        val dialogBinding = DialogEditItemVisitMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        customDialog.show()

        dialogBinding.tvTitle.text = name
        dialogBinding.etDescription.setText(description)
        dialogBinding.etWorkDone.setText(workDone)

        dialogBinding.btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        dialogBinding.imageButtonCancel.setOnClickListener {
            customDialog.dismiss()
        }

//        Item Code
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
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].item_code)
                    itemCodeList = result as ArrayList<ItemCodeDataItem>
                    Log.d("myapp", itemCodeList.toString())

                    val temp = arrayListOf<String>()
                    itemCodeList.forEach {
                        temp.add(it.item_code)
                    }

                    val itemCodeAdapter = ArrayAdapter(
                        this@EditVisitMaintenanceActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    itemCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    dialogBinding.spinnerItemCode.adapter = itemCodeAdapter

                    dialogBinding.spinnerItemCode.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {

                                val selectedItemCode = itemCodeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }

                    val oldItemCode = itemCode
                    var index = -1
                    Log.d("myapp", "$index  ${oldItemCode} $itemCodeList")
                    itemCodeList.forEachIndexed { position, data ->
                        if (data.item_code == oldItemCode) {
                            index = position
                        }
                    }
                    dialogBinding.spinnerItemCode.setSelection(index)
                }
            }
        }
//        Sales Person
        salesPersonList = arrayListOf()

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                SalesPersonRetrofitInstance.setBaseUrl(baseUrl)
                SalesPersonRetrofitInstance.api.getSalesPerson(token)
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
                    salesPersonList = result as ArrayList<SalesPersonDataItem>
                    Log.d("myapp", salesPersonList.toString())

                    val temp = arrayListOf<String>()
                    salesPersonList.forEach {
                        temp.add(it.name)
                    }

                    val salesPersonAdapter = ArrayAdapter(
                        this@EditVisitMaintenanceActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    salesPersonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    dialogBinding.spinnerSalesPerson.adapter = salesPersonAdapter

                    dialogBinding.spinnerSalesPerson.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {

                                val selectedSalesPerson = salesPersonList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }

                    val oldSalesPerson = salesPerson
                    var index = -1
                    Log.d("myapp", "$index  ${oldSalesPerson} $salesPersonList")
                    salesPersonList.forEachIndexed { position, data ->
                        if (data.name == oldSalesPerson) {
                            index = position
                        }
                    }
                    dialogBinding.spinnerSalesPerson.setSelection(index)
                }
            }
        }

        dialogBinding.btnSave.setOnClickListener {

            if (dialogBinding.etDescription.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
            } else if (dialogBinding.etWorkDone.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Work Done", Toast.LENGTH_SHORT).show()
            } else {
                val newElement = EditVisitItemList(
                    itemCode = dialogBinding.spinnerItemCode.selectedItem.toString(),
                    salesPerson = dialogBinding.spinnerSalesPerson.selectedItem.toString(),
                    description = dialogBinding.etDescription.text.toString(),
                    workDone = dialogBinding.etWorkDone.text.toString()
                )
                newEditVisitItemList.add(newElement)
                rvEditAdapter.editVisitItem(
                    newElement
                )
                Log.d("my btnSave", newElement.toString())
                customDialog.dismiss()

            }
        }
    }

    private fun submit() {

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        val list =
            arrayListOf<com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.Purpose>()

        if (binding.tvMaintenanceDate.text.isEmpty()) {
            Toast.makeText(this, "Select Maintenance date", Toast.LENGTH_SHORT).show()
        } else if (newEditVisitItemList.isEmpty()) {
            Toast.makeText(this, "Add one item at least", Toast.LENGTH_SHORT).show()
        } else {

            GlobalScope.launch(Dispatchers.IO) {

                val response = try {

                    newEditVisitItemList.forEach {
                        list.add(
                            com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.Purpose(
                                it.itemCode,
                                it.salesPerson,
                                it.description,
                                it.workDone
                            )
                        )
                    }

                    requestModel.data.purposes = list
                    requestModel.data.customer = binding.spinnerCustomer.selectedItem.toString()
                    requestModel.data.completion_status = binding.spinnerCompletionStatus.selectedItem.toString()
                    requestModel.data.maintenance_type = binding.spinnerMaintenanceType.selectedItem.toString()
                    requestModel.data.mntc_date = binding.tvMaintenanceDate.text.toString()

                    VisitMaintenanceInstance.setBaseUrl(baseUrl)
                    VisitMaintenanceInstance.api.editVisitMaintenance(
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
                    Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                Log.d(
                    "my app4",
                    list.toString() + binding.spinnerCustomer.selectedItem.toString()
                )

                Log.d("my is it here", response.toString())
                Log.d("my app4", response.raw().toString())
                Log.d("my app55", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val result = response.body()!!.data
                        Log.d("my app66", "${response.body()} request send successfully")
                        Toast.makeText(
                            this@EditVisitMaintenanceActivity,
                            "Saved Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }else{
                    Toast.makeText(
                        this@EditVisitMaintenanceActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }
    }

    private fun editCustomer(response1: Response<EditVisitMaintenance?>): String {
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
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
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app7", result[0].name)
                    customerList = result as ArrayList<CustomerDataItem>
                    Log.d("myapp", customerList.toString())

                    val temp = arrayListOf<String>()
                    customerList.forEach {
                        temp.add(it.name)
                    }

                    val customerAdapter = ArrayAdapter(
                        this@EditVisitMaintenanceActivity,
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
                                val selectedSalesPerson = customerList[position]
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
            }else{
                Toast.makeText(
                    this@EditVisitMaintenanceActivity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        return binding.spinnerCustomer.toString()
    }

    private fun completionStatus(response: Response<EditVisitMaintenance?>): String {
        val completionStatusData = listOf("Partially Completed", "Fully Completed")

        val completionStatusAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, completionStatusData)

        completionStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerCompletionStatus.adapter = completionStatusAdapter

        binding.spinnerCompletionStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCompletionStatus = completionStatusData[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@EditVisitMaintenanceActivity,
                        "Please choose item",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        val oldStatus = response.body()!!.data.completion_status
        val index = completionStatusData.indexOf(oldStatus)
        binding.spinnerCompletionStatus.setSelection(index)
        return binding.spinnerCompletionStatus.toString()
    }

    private fun maintenanceDate() {
        binding.tvMaintenanceDate.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvMaintenanceDate.text = selectedDate
                }, year, month, day
            ).show()
        }
    }

    private fun maintenanceType(response: Response<EditVisitMaintenance?>): String {
        val maintenanceTypeData = listOf("Scheduled", "Unscheduled", "Breakdown")

        val maintenanceTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, maintenanceTypeData)

        maintenanceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerMaintenanceType.adapter = maintenanceTypeAdapter

        binding.spinnerMaintenanceType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val maintenanceTypeStatus = maintenanceTypeData[position]

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@EditVisitMaintenanceActivity,
                        "Please choose item",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val oldStatus = response.body()!!.data.completion_status
        val index = maintenanceTypeData.indexOf(oldStatus)
        binding.spinnerCompletionStatus.setSelection(index)
        return binding.spinnerMaintenanceType.toString()
    }

    private fun showCustomDialogBox() {
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmDeleteVisitMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener() {
            this@EditVisitMaintenanceActivity.finish()
            GlobalScope.launch {
                val response = try {
                    VisitMaintenanceInstance.setBaseUrl(baseUrl)
                    VisitMaintenanceInstance.api.deleteVisitMaintenance(token, id)
                } catch (e: Exception) {
                    return@launch
                }
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditVisitMaintenanceActivity,
                            "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@EditVisitMaintenanceActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
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
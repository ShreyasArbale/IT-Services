package com.example.maintenance.maintenance.edit_maintenance

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
import com.example.maintenance.SalesPerson.SalesPersonRetrofitInstance
import com.example.maintenance.SalesPerson.model_sales_person.SalesPersonDataItem
import com.example.maintenance.maintenance.MaintenanceRetrofitInstance
import com.example.maintenance.databinding.ActivityEditMaintenanceBinding
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.DialogConfirmDeleteMaintenanceBinding
import com.example.maintenance.databinding.DialogConfirmItemDeleteMaintenanceBinding
import com.example.maintenance.databinding.DialogEditItemMaintenanceBinding
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.add_maintenance.ItemList
import com.example.maintenance.maintenance.add_maintenance.ItemListAdapter
import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.AddMaintenanceRequestModel
import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.Data
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.EditMaintenance
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.Item
import com.example.maintenance.maintenance.edit_maintenance.edit_maintenance_model.Schedule
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.customer.model_customer.CustomerDataItem
import com.example.maintenance.databinding.DialogAddItemIssueBinding
import com.example.maintenance.databinding.DialogAddItemMaintenanceBinding
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

class EditMaintenanceActivity : AppCompatActivity(), MaintenanceItemListener {

    private lateinit var binding: ActivityEditMaintenanceBinding
    private var id = ""
    private var idx = ""

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var maintenanceItemList: ArrayList<Item>
    private lateinit var maintenanceScheduleList: ArrayList<Schedule>
    private lateinit var itemCodeList: ArrayList<ItemCodeDataItem>
    private lateinit var personList: ArrayList<SalesPersonDataItem>
    private lateinit var salesPersonList: ArrayList<SalesPersonDataItem>

    private lateinit var rvAdapter: EditItemMaintenanceAdapter
    private lateinit var rvAddAdapter: ItemListAdapter
    private lateinit var rvEditAdapter: EditItemListAdapter
    private lateinit var rvScheduleAdapter: EditScheduleMaintenanceAdapter


    private var newEditItemList: ArrayList<EditItemList> = arrayListOf()
    private var newItemList: ArrayList<ItemList> = arrayListOf()

    private lateinit var requestModel: AddMaintenanceRequestModel
    private var mProgressDialog: Dialog? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    private lateinit var selectedItemCode: ItemCodeDataItem

    private lateinit var baseUrl: String
    private lateinit var dialogBinding: DialogAddItemMaintenanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditMaintenanceBinding.inflate(layoutInflater)

        setContentView(binding.root)
        id = intent.getStringExtra("id").toString()
        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        maintenanceItemList = arrayListOf()

        getData()
//        dialog()

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            deleteMaintenance()
        }

        binding.buttonSubmit.setOnClickListener {
            submit()
        }
        requestModel = AddMaintenanceRequestModel(data = Data("", arrayListOf(), ""))

//        layoutManager = LinearLayoutManager(this)
//        binding.rvAddMaintenance.layoutManager = layoutManager
//        rvAddAdapter = ItemListAdapter(itemList = arrayListOf())
//        binding.rvAddMaintenance.adapter = rvAddAdapter

        layoutManager = LinearLayoutManager(this)
        binding.rvAddMaintenance.layoutManager = layoutManager
        rvEditAdapter = EditItemListAdapter(itemList = arrayListOf())
        binding.rvAddMaintenance.adapter = rvEditAdapter

    }

    private fun getData() {
        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        showCustomProgressDialog()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        MaintenanceRetrofitInstance.setBaseUrl(baseUrl)
        MaintenanceRetrofitInstance.api.getEditMaintenance(token, id).enqueue(object :
            Callback<EditMaintenance?> {

            override fun onResponse(
                call: Call<EditMaintenance?>,
                response: Response<EditMaintenance?>
            ) {
                editCustomer(response)
                transactionDate()


                binding.tvTitle.text = response.body()!!.data.name
                binding.tvTransactionDate.text = response.body()!!.data.transaction_date

                getItem()
                getSchedule()

            }

            override fun onFailure(call: Call<EditMaintenance?>, t: Throwable) {
                Toast.makeText(
                    this@EditMaintenanceActivity,
                    "${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getItem() {

        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        super.onResume()

        GlobalScope.launch(Dispatchers.IO) {

            val response = try {
                MaintenanceRetrofitInstance.setBaseUrl(baseUrl)
                MaintenanceRetrofitInstance.api.getItemMaintenance(token, id)
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
                    val result = response.body()!!.data.items

//                    Log.d("my app", result[0].name)
                    maintenanceItemList = result
                    binding.rvAddMaintenance.apply {
                        rvAdapter = EditItemMaintenanceAdapter(
                            maintenanceItemList,
                            itemListListener = this@EditMaintenanceActivity
                        )
                        adapter = rvAdapter
                        layoutManager = LinearLayoutManager(this@EditMaintenanceActivity)
                    }
                }
            }
            Log.d("myapp", "onResume called")
        }

    }

    private fun getSchedule() {
        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                MaintenanceRetrofitInstance.setBaseUrl(baseUrl)
                MaintenanceRetrofitInstance.api.getScheduleMaintenance(token, id)
            } catch (e: IOException) {
//                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                Log.d("myapp", "${e} ${e.message}")
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: Exception) {
                Log.e("my app schedule e", e.message.toString())
//                    Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Log.d("my app schedule", response.toString())
            if (response.isSuccessful && response.body() != null) {
                hideProgressDialog()

                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data.schedules

                    maintenanceScheduleList = result
                    binding.rvScheduleMaintenance.apply {
                        rvScheduleAdapter = EditScheduleMaintenanceAdapter(
                            maintenanceScheduleList
                        )
                        adapter = rvScheduleAdapter
                        layoutManager = LinearLayoutManager(this@EditMaintenanceActivity)

                        Log.d("my app schedule d", response.body().toString())
                    }
                }
            }
            Log.d("myapp", "onResume called")
        }


    }

    override fun onMaintenanceItemClick(
        name: String,
        itemCode: String,
        startDate: String,
        endDate: String,
        periodicity: String,
        noOfVisits: String,
        salesPerson: String
    ) {
        intent.putExtra("id", name)
        dialogItemShow(
            name,
            itemCode,
            startDate,
            endDate,
            periodicity,
            noOfVisits,
            salesPerson
        )
    }

    private fun dialogItemShow(
        name: String,
        item_code: String,
        startDate: String,
        endDate: String,
        periodicity: String,
        noOfVisits: String,
        salesPerson: String
    ) {
        super.onResume()


        val customDialog = Dialog(this)
        val dialogBinding = DialogEditItemMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        customDialog.show()

        dialogBinding.tvTitle.text = name
        dialogBinding.tvStartDate.text = startDate
        dialogBinding.tvEndData.text = endDate
        dialogBinding.etVisits.setText(noOfVisits)

        dialogBinding.btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        dialogBinding.imageButtonCancel.setOnClickListener {
            customDialog.dismiss()
        }

        Log.d("my noOfVisits", dialogBinding.etVisits.toString())

        itemCode(baseUrl,token,item_code )
        startDate()
        endDate()
        periodicityDate(periodicity)
        salesPerson(baseUrl,token,salesPerson)

//        Save Button
        dialogBinding.btnSave.setOnClickListener {

            if (dialogBinding.etVisits.text!!.isEmpty()) {
                Toast.makeText(this, "Enter number of visits", Toast.LENGTH_SHORT).show()
            } else {
                val newElement = EditItemList(
                    itemCode = selectedItemCode.name,
//                    itemCode = selectedItemCode.item_code,
                    startDate = dialogBinding.tvStartDate.text.toString(),
                    endDate = dialogBinding.tvEndData.text.toString(),
                    periodicity = dialogBinding.spinnerPeriodicity.selectedItem.toString(),
                    visits = dialogBinding.etVisits.text.toString(),
                    salesPerson = dialogBinding.spinnerSalesPerson.selectedItem.toString()
                )
                newEditItemList.add(newElement)
                rvEditAdapter.editItem(
                    newElement,
                    0
                )
                Log.d("my btnSave", newElement.itemCode)
                Log.d("my btnSave", newElement.toString())
                customDialog.dismiss()

            }
        }
    }

    private fun submit() {

        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        val list =
            arrayListOf<com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.Item>()

        if (binding.tvTransactionDate.text.isEmpty()) {
            Toast.makeText(this, "Select transaction date", Toast.LENGTH_SHORT).show()
        } else if (newEditItemList.isEmpty()) {
            Toast.makeText(this, "Add one item at least", Toast.LENGTH_SHORT).show()
        } else {

            GlobalScope.launch(Dispatchers.IO) {

                val response = try {

                    newEditItemList.forEach {
                        list.add(
                            com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.Item(
                                it.itemCode,
                                it.startDate,
                                it.endDate,
                                it.periodicity,
                                it.visits.toInt(),
                                it.salesPerson
                            )
                        )
                    }

                    requestModel.data.items = list
                    requestModel.data.customer = binding.spinnerCustomer.selectedItem.toString()
                    requestModel.data.transaction_date = binding.tvTransactionDate.text.toString()

                    MaintenanceRetrofitInstance.setBaseUrl(baseUrl)

                    MaintenanceRetrofitInstance.api.editMaintenance(
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

                Log.d(
                    "my app4",
                    list.toString() + binding.spinnerCustomer.selectedItem.toString() + binding.tvTransactionDate.text.toString()
                )

                Log.d("my app4", response.raw().message())
                Log.d("my app5", response.body().toString())
                Log.d("my app4", response.body().toString())
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        val result = response.body()!!.data
                        Log.d("my app66", "${response.body()} request send successfully")
                        Toast.makeText(
                            this@EditMaintenanceActivity,
                            "Saved Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }
            }
        }
    }

    private fun itemCode(baseUrl: String, token: String, item_code: String){
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
                    Log.d("my app7", result[0].item_name)
                    itemCodeList = result as ArrayList<ItemCodeDataItem>
                    Log.d("myapp", itemCodeList.toString())

                    val temp = arrayListOf<String>()
                    itemCodeList.forEach {
                        temp.add("${it.name}")
                    }

                    val itemCodeAdapter = ArrayAdapter(
                        this@EditMaintenanceActivity,
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

                                selectedItemCode = itemCodeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }

                    val oldItemCode = item_code
                    var index = -1
                    Log.d("myapp", "$index  ${oldItemCode} $itemCodeList")
                    itemCodeList.forEachIndexed { position, data ->
                        if (data.name == oldItemCode) {
                            index = position
                        }
                    }
                    dialogBinding.spinnerItemCode.setSelection(index)
                    Log.d("myapp20", "$index  ${oldItemCode} ")
                }
            }
        }
    }

    private fun transactionDate() {
        binding.tvTransactionDate.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvTransactionDate.text = selectedDate
                }, year, month, day
            ).show()
        }
    }

    private fun editCustomer(response1: Response<EditMaintenance?>): String {

        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

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
            Log.d("my app6", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
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
                        this@EditMaintenanceActivity,
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

    private fun startDate(){
        dialogBinding.tvStartDate.setOnClickListener {      //btnChooseStartDate

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    dialogBinding.tvStartDate.text = selectedDate

                }, year, month, day
            ).show()
        }
    }

    private fun endDate(){
        dialogBinding.tvEndData.setOnClickListener {     //btnChooseEndDate

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    dialogBinding.tvEndData.text = selectedDate

                }, year, month, day
            ).show()
        }
    }

    private fun salesPerson(baseUrl: String, token: String, salesPerson: String){
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
                        this@EditMaintenanceActivity,
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
    }

    private fun periodicityDate(periodicity: String){
        val periodicityData =
            listOf("Weekly", "Monthly", "Quarterly", "Half Yearly", "Yearly", "Random")

        val periodicityAdapter = ArrayAdapter(
            this@EditMaintenanceActivity,
            android.R.layout.simple_spinner_item,
            periodicityData
        )

        periodicityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dialogBinding.spinnerPeriodicity.adapter = periodicityAdapter

        dialogBinding.spinnerPeriodicity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedPeriodicity = periodicityData[position]

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        val oldPeriodicity = periodicity
        val index = periodicityData.indexOf(oldPeriodicity)
        dialogBinding.spinnerPeriodicity.setSelection(index)
    }

    override fun onMaintenanceDeleteItemClick(name: String) {
        intent.putExtra("id", name)
        deleteMaintenanceItem(name)
    }

    private fun deleteMaintenanceItem(name: String) {

        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmItemDeleteMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener {
            showCustomProgressDialog()
            this@EditMaintenanceActivity
            GlobalScope.launch {
                val response = try {
                    MaintenanceRetrofitInstance.setBaseUrl(baseUrl)
                    MaintenanceRetrofitInstance.api.deleteMaintenanceItem(token, id)
                } catch (e: Exception) {
                    Log.e("my yes", e.message.toString())
                    return@launch
                }
                Log.d("my yes2", response.message().toString())
                if (response.isSuccessful) {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditMaintenanceActivity,
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

    private fun deleteMaintenance() {
        binding.buttonDelete.setOnClickListener {

            showCustomDialogBox()

        }
    }

    private fun showCustomDialogBox() {

        val baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmDeleteMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener() {
            this@EditMaintenanceActivity.finish()
            GlobalScope.launch {
                val response = try {
                    MaintenanceRetrofitInstance.setBaseUrl(baseUrl)
                    MaintenanceRetrofitInstance.api.deleteMaintenance(token, id)
                } catch (e: Exception) {
                    return@launch
                }
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditMaintenanceActivity,
                            "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }
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
        mProgressDialog!!.setContentView(com.example.maintenance.R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

}
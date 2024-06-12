package com.example.maintenance.visit_maintenance.add_visit_maintenance

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
import com.example.maintenance.databinding.ActivityAddVisitMaintenanceBinding
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.SalesPerson.SalesPersonRetrofitInstance
import com.example.maintenance.SalesPerson.model_sales_person.SalesPersonDataItem
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.DialogAddItemVisitMaintenanceBinding
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.visit_maintenance.VisitMaintenanceInstance
import com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.AddVisitMaintenanceModel
import com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.Data
import com.example.maintenance.visit_maintenance.add_visit_maintenance.model_add_Visit_Maintenance.Purpose
import com.example.maintenance.customer.model_customer.CustomerDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Calendar

class AddVisitMaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVisitMaintenanceBinding

    private var newVisitItemList: ArrayList<VisitItemList> = arrayListOf()
    private lateinit var rvVisitAdapter: VisitListAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null

    private lateinit var requestVisitModel: AddVisitMaintenanceModel

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var itemList: ArrayList<ItemCodeDataItem>
    private lateinit var personList: ArrayList<SalesPersonDataItem>

    private lateinit var  baseUrl : String

    private lateinit var selectedItem : ItemCodeDataItem

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddVisitMaintenanceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        requestVisitModel = AddVisitMaintenanceModel(data = Data("","", "","", arrayListOf()))

        customer()
        transactionDate()
        completionStatus()
        maintenanceType()
        dialog()

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        layoutManager = LinearLayoutManager(this)
        binding.rvAddVisitMaintenance.layoutManager = layoutManager
        rvVisitAdapter = VisitListAdapter(visitItemList = arrayListOf())
        binding.rvAddVisitMaintenance.adapter = rvVisitAdapter

    }

    private fun dialog(){
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        val customDialog = Dialog(this)
        val dialogBinding = DialogAddItemVisitMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)


        binding.buttonSubmit.setOnClickListener {

            val list = arrayListOf<Purpose>()

            if (binding.tvMaintenanceDate.text.isEmpty()){
                Toast.makeText(this, "Select maintenance date", Toast.LENGTH_SHORT).show()
            }else if(newVisitItemList.isEmpty()){
                Toast.makeText(this, "Add one item at least", Toast.LENGTH_SHORT).show()
            } else{

                GlobalScope.launch(Dispatchers.IO) {

                    val response = try {
                        newVisitItemList.forEach {
                            list.add(
                                Purpose(
                                    it.itemCode,
                                    it.salesPerson,
                                    it.description,
                                    it.work_done
                                )
                            )
                        }

                        requestVisitModel.data.purposes = list
                        requestVisitModel.data.customer = binding.spinnerCustomer.selectedItem.toString()
                        requestVisitModel.data.completion_status = binding.spinnerCompletionStatus.selectedItem.toString()
                        requestVisitModel.data.mntc_date = binding.tvMaintenanceDate.text.toString()
                        requestVisitModel.data.maintenance_type = binding.spinnerMaintenanceType.selectedItem.toString()

                        VisitMaintenanceInstance.setBaseUrl(baseUrl)
                        VisitMaintenanceInstance.api.addVisitMaintenance(token, requestVisitModel)

                    } catch (e: IOException) {
                        Log.e("my error app1", e.message.toString())
                        Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
                        return@launch
                    } catch (e: HttpException) {
                        Log.e("my error app2", e.message.toString())
                        Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                        return@launch
                    } catch (e: Exception) {
                        Log.e("my error app33", e.message.toString())
                        Toast.makeText(applicationContext, "http error", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    Log.d("my app42", list.toString()+ binding.spinnerCustomer.selectedItem.toString()+
                            binding.spinnerCompletionStatus.selectedItem.toString()+binding.tvMaintenanceDate.text.toString()+
                            binding.spinnerMaintenanceType.selectedItem.toString())

                    Log.d("my app4", response.code().toString())
                    Log.d("my app5", response.body().toString())
                    if (response.isSuccessful && response.body() != null) {
                        withContext(Dispatchers.Main) {
                            val result = response.body()!!.data
                            Log.d("my app6", "request send successfully")
                            Toast.makeText(this@AddVisitMaintenanceActivity, "Saved Successful!", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }else{
                        Log.d("my app6.1", "Request Failed!!")
                    }
                }

            }
        }

        binding.btnAddVisitItem.setOnClickListener {
            customDialog.show()
        }

        dialogBinding.btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        dialogBinding.imageButtonCancel.setOnClickListener {
            customDialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            if (dialogBinding.etDescription.text!!.isEmpty()) {
                Toast.makeText(this, "Enter number of visits", Toast.LENGTH_SHORT).show()
            } else if (dialogBinding.etWorkDone.text!!.isEmpty()) {
                Toast.makeText(this, "Select start date", Toast.LENGTH_SHORT).show()
            }  else {
                val newElement = VisitItemList(
                    itemCode = selectedItem.name,
                    salesPerson = dialogBinding.spinnerSalesPerson.selectedItem.toString(),
                    description = dialogBinding.etDescription.text.toString(),
                    work_done = dialogBinding.etWorkDone.text.toString(),
                )
                newVisitItemList.add(newElement)
                rvVisitAdapter.addVisitItem(
                    newElement
                )
                customDialog.dismiss()
            }
        }

        //      Item Code
        itemList = arrayListOf()
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
                    itemList = result as ArrayList<ItemCodeDataItem>

                    val temp = arrayListOf<String>()
                    itemList.forEach {
                        temp.add("${it.name}\n(${it.item_name})")
                    }

                    val itemAdapter = ArrayAdapter(
                        this@AddVisitMaintenanceActivity, android.R.layout.simple_spinner_item, temp
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
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }
        }

//      Sales Person
        personList = arrayListOf()
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
            Log.d("my app9", response.headers().toString())
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val result = response.body()!!.data
                    Log.d("my app10", result[0].name)
                    personList = result as ArrayList<SalesPersonDataItem>

                    val temp = arrayListOf<String>()
                    personList.forEach {
                        temp.add(it.name)
                    }

                    val personAdapter = ArrayAdapter(
                        this@AddVisitMaintenanceActivity,
                        android.R.layout.simple_spinner_item,
                        temp
                    )

                    personAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    dialogBinding.spinnerSalesPerson.adapter = personAdapter

                    dialogBinding.spinnerSalesPerson.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedPerson = personList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
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
                        this@AddVisitMaintenanceActivity,
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

    private fun transactionDate() {
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

    private fun completionStatus() : String{
        val completionStatusData = listOf("Partially Completed", "Fully Completed")

        val completionStatusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, completionStatusData)

        completionStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerCompletionStatus.adapter = completionStatusAdapter

        binding.spinnerCompletionStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedCompletionStatus = completionStatusData[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@AddVisitMaintenanceActivity,
                    "Please choose item",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.spinnerCompletionStatus.toString()
    }

    private fun maintenanceType() : String{
        val maintenanceTypeData = listOf("Scheduled", "Unscheduled","Breakdown")

        val maintenanceTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, maintenanceTypeData)

        maintenanceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerMaintenanceType.adapter = maintenanceTypeAdapter

        binding.spinnerMaintenanceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val maintenanceTypeStatus = maintenanceTypeData[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    this@AddVisitMaintenanceActivity,
                    "Please choose item",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.spinnerMaintenanceType.toString()
    }
}
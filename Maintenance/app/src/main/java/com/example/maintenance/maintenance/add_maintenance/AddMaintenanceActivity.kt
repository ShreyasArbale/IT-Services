package com.example.maintenance.maintenance.add_maintenance

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.media.session.MediaSession.Token
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maintenance.maintenance.MaintenanceRetrofitInstance
import com.example.maintenance.databinding.ActivityAddMaintenanceBinding
import com.example.maintenance.maintenance.ItemCode.ItemCodeRetrofitInstance
import com.example.maintenance.maintenance.ItemCode.model_item_code.ItemCodeDataItem
import com.example.maintenance.SalesPerson.SalesPersonRetrofitInstance
import com.example.maintenance.SalesPerson.model_sales_person.SalesPersonDataItem
import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.AddMaintenanceRequestModel
import com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.Data
import com.example.maintenance.customer.CustomerRetrofitInstance
import com.example.maintenance.databinding.DialogAddItemMaintenanceBinding
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.customer.model_customer.CustomerDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.http.Url
import java.io.IOException
import java.util.Calendar

class AddMaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaintenanceBinding
    private var mProgressDialog: Dialog? = null

    private lateinit var token: String
    private lateinit var baseUrl: String
    private lateinit var customDialog: Dialog
    private lateinit var dialogBinding: DialogAddItemMaintenanceBinding

    private lateinit var customerList: ArrayList<CustomerDataItem>
    private lateinit var itemList: ArrayList<ItemCodeDataItem>
    private lateinit var personList: ArrayList<SalesPersonDataItem>

    private lateinit var selectedItem: ItemCodeDataItem

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var rvAdapter: ItemListAdapter
    private var newItemList: ArrayList<ItemList> = arrayListOf()
    private lateinit var requestModel: AddMaintenanceRequestModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        token = SharedPreferenceManager.getInstance(applicationContext).getToken
        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        requestModel = AddMaintenanceRequestModel(data = Data("", arrayListOf(), ""))

        customer(baseUrl)
        transactionDate()

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.btnAddItem.setOnClickListener {
            dialog(baseUrl)
            customDialog.show()
        }

        binding.buttonSubmit.setOnClickListener {
            submit(baseUrl)
        }

        layoutManager = LinearLayoutManager(this)
        binding.rvAddMaintenance.layoutManager = layoutManager
        rvAdapter = ItemListAdapter(itemList = arrayListOf())
        binding.rvAddMaintenance.adapter = rvAdapter

    }

    private fun dialog(baseUrl: String) {
        customDialog = Dialog(this)
        dialogBinding = DialogAddItemMaintenanceBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.imageButtonCancel.setOnClickListener {
            customDialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        startDate()
        endDate()
        periodicity()
        itemCode(baseUrl)
        salesPerson(baseUrl)

        dialogBinding.btnSave.setOnClickListener {
            if (dialogBinding.etVisits.text!!.isEmpty()) {
                Toast.makeText(this, "Enter number of visits", Toast.LENGTH_SHORT).show()
            } else if (dialogBinding.tvStartDate.text!!.isEmpty()) {
                Toast.makeText(this, "Select start date", Toast.LENGTH_SHORT).show()
            } else if (dialogBinding.tvEndData.text!!.isEmpty()) {
                Toast.makeText(this, "Select end date", Toast.LENGTH_SHORT).show()
            } else {
//                val item_code=itemList.firstOrNull { it.item_name == dialogBinding.spinnerItemCode.selectedItem.toString() }
//                if (item_code != null) {
//                    Log.d("item code", item_code.name)
//                }
                val newElement = ItemList(
                    itemCode = selectedItem.name,
                    itemName = dialogBinding.spinnerItemCode.selectedItem.toString(),
                    startDate = dialogBinding.tvStartDate.text.toString(),
                    endDate = dialogBinding.tvEndData.text.toString(),
                    periodicity = dialogBinding.spinnerPeriodicity.selectedItem.toString(),
                    visits = dialogBinding.etVisits.text.toString(),
                    salesPerson = dialogBinding.spinnerSalesPerson.selectedItem.toString()
                )
                newItemList.add(newElement)
                rvAdapter.addItem(
                    newElement
                )
                customDialog.dismiss()
            }
        }
    }

    private fun itemCode(baseUrl: String) {
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
                        this@AddMaintenanceActivity, android.R.layout.simple_spinner_item, temp
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
    }

    private fun startDate() {
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

    private fun endDate() {
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

    private fun periodicity() {
        val periodicityData =
            listOf("Weekly", "Monthly", "Quarterly", "Half Yearly", "Yearly", "Random")

        var periodicityAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, periodicityData)

        periodicityAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        dialogBinding.spinnerPeriodicity.adapter = periodicityAdapter

        dialogBinding.spinnerPeriodicity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var selectedPeriodicity = periodicityData[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun salesPerson(baseUrl: String) {
        personList = arrayListOf()
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                SalesPersonRetrofitInstance.setBaseUrl(baseUrl)
                SalesPersonRetrofitInstance.api.getSalesPerson(token)
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
                    personList = result as ArrayList<SalesPersonDataItem>

                    val temp = arrayListOf<String>()
                    personList.forEach {
                        temp.add(it.name)
                    }

                    val personAdapter = ArrayAdapter(
                        this@AddMaintenanceActivity,
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

    private fun submit(baseUrl: String) {
        showCustomProgressDialog()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken
        val list =
            arrayListOf<com.example.maintenance.maintenance.add_maintenance.model_add_maintenance.Item>()

        if (binding.tvTransactionData.text.isEmpty()) {
            Toast.makeText(this, "Select transaction date", Toast.LENGTH_SHORT).show()
        } else if (newItemList.isEmpty()) {
            Toast.makeText(this, "Add one item at least", Toast.LENGTH_SHORT).show()
        } else {

            GlobalScope.launch(Dispatchers.IO) {

                val response = try {

                    newItemList.forEach {
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
                    requestModel.data.transaction_date = binding.tvTransactionData.text.toString()

                    MaintenanceRetrofitInstance.setBaseUrl(baseUrl)
                    MaintenanceRetrofitInstance.api.addMaintenance(token, requestModel)

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

                if (response.isSuccessful && response.body() != null) {
                    hideProgressDialog()
                    withContext(Dispatchers.Main) {
                        val result = response.body()!!.data
                        Log.d("my save success", response.message())
                        Toast.makeText(
                            this@AddMaintenanceActivity,
                            "Saved Successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                } else {
                    Log.d("my save fail", response.message())
                    Toast.makeText(
                        this@AddMaintenanceActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun customer(baseUrl: String) {
        customerList = arrayListOf()
        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                CustomerRetrofitInstance.setBaseUrl(baseUrl)
                CustomerRetrofitInstance.api.getCustomer(token)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
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
                        this@AddMaintenanceActivity,
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
        binding.tvTransactionData.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvTransactionData.text = selectedDate
                }, year, month, day
            ).show()
        }
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
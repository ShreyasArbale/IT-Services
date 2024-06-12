package com.example.maintenance.issue

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maintenance.R
import com.example.maintenance.databinding.ActivityHomeIssueBinding
import com.example.maintenance.issue.add_issue.AddIssueActivity
import com.example.maintenance.issue.edit_issue.EditIssueActivity
import com.example.maintenance.issue.model_issue.IssueItem
import com.example.maintenance.storage.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeIssueActivity : AppCompatActivity(), IssueListener {

    private lateinit var binding: ActivityHomeIssueBinding
    private lateinit var usersList: ArrayList<IssueItem>
    private lateinit var rvAdapter: IssueAdapter
    private var mProgressDialog: Dialog? =null

    private lateinit var  baseUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeIssueBinding.inflate(layoutInflater)

        setContentView(binding.root)

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl

        usersList = arrayListOf()

        binding.flotAdd.setOnClickListener {
            val intent = Intent(this@HomeIssueActivity, AddIssueActivity::class.java)
            startActivity(intent)
        }
        binding.imageButtonBack.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        showCustomProgressDialog()
        super.onResume()

        val token = SharedPreferenceManager.getInstance(applicationContext).getToken

        Log.d("myapp", "onResume called")
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                IssueRetrofitInstance.setBaseUrl(baseUrl)
                IssueRetrofitInstance.api.getIssue(token)
            } catch (e: IOException) {
//                Toast.makeText(applicationContext, "app error", Toast.LENGTH_SHORT).show()
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
                    usersList = result
                    binding.rvMain.apply {
                        rvAdapter = IssueAdapter(usersList, this@HomeIssueActivity)
                        adapter = rvAdapter
                        layoutManager = LinearLayoutManager(this@HomeIssueActivity)
//                        (layoutManager as LinearLayoutManager).reverseLayout = true
//                        (layoutManager as LinearLayoutManager).stackFromEnd = true
                    }
                }
            }
        }
    }

    override fun onIssueClick(id: String) {
        val intent = Intent(this@HomeIssueActivity, EditIssueActivity::class.java)
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
package com.example.maintenance.login

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.maintenance.LoginRetrofitInstance
import com.example.maintenance.MainActivity
import com.example.maintenance.R
import com.example.maintenance.databinding.ActivityLoginBinding
import com.example.maintenance.login.model_login.LoginResponse
import com.example.maintenance.storage.SharedPreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (SharedPreferenceManager.getInstance(this).isLoggedIn){
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }

        binding.btnLogin.setOnClickListener {

            val url = binding.etBaseUrl.text.toString().trim()
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            Log.d("my url new", email + url + password)

            if (email.isEmpty()) {
                binding.etUsername.error = "Email required"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (url.isEmpty()) {
                binding.etBaseUrl.error = "URL required"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }
            showCustomProgressDialog()

            LoginRetrofitInstance.setBaseUrl(url)
            LoginRetrofitInstance.api.userLogin(email, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            hideProgressDialog()

                            response.body()?.key_details?.let { it1 ->
                                Log.d("my key details", it1.toString())
                                SharedPreferenceManager.getInstance(applicationContext).saveUser(
                                    it1
                                )
                            }
                            Log.d("my login success", response.message().toString())
                            SharedPreferenceManager.getInstance(applicationContext)
                                .saveLoginStatus()
                            Log.d("url", url)
                            Log.d("my login fail", response.raw().toString())
                            SharedPreferenceManager.getInstance(applicationContext).saveBaseUrl(url)
                            SharedPreferenceManager.getInstance(applicationContext).saveUserName(email)
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        } else {
                            hideProgressDialog()
                            Log.d("my login fail", response.raw().toString())
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        hideProgressDialog()
                        Toast.makeText(applicationContext, "Error ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("my onFailure", "Error ${t.message}")
                    }
                })
        }

    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
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

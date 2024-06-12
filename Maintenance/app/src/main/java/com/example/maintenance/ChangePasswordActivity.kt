package com.example.maintenance

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.maintenance.databinding.ActivityChangePasswordBinding
import com.example.maintenance.storage.SharedPreferenceManager
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var baseUrl: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        baseUrl = SharedPreferenceManager.getInstance(applicationContext).getBaseUrl
        token = SharedPreferenceManager.getInstance(applicationContext).getToken

        binding.btnPasswordSave.setOnClickListener {
            val currentPassword = binding.editTextCurrentPassword.text.toString()
            val newPassword = binding.editTextNewPassword.text.toString()
            val reenterNewPassword = binding.editTextReenterNewPassword.text.toString()

            if (newPassword != reenterNewPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val changePasswordDetails = ChangePassword(
                current_password = currentPassword,
                new_password = newPassword
            )

            changePassword(this, changePasswordDetails) { success ->
                if (success) {
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                    Log.d("my Failed", "failed")
                }
            }
        }
    }

    private fun changePassword(
        context: Context,
        orderDetails: ChangePassword,
        callback: (Boolean) -> Unit
    ) {
        val client = OkHttpClient()
        val gson = Gson()

        val json = gson.toJson(orderDetails)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Log.d("my body", body.toString())
        Log.d("my order details", orderDetails.toString())

        val request = Request.Builder()
            .url("$baseUrl/api/method/mobile.mobile_env.app.change_password")
            .post(body)
            .addHeader("Authorization", token)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT)
                            .show()
                        callback(true)
                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Failed to change password",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("my false response", "failed, ${response.message}")
                        callback(false)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        context,
                        "Unable to change password, ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("my failure", e.toString())
                }
                callback(false)
            }

        })
    }
}

data class ChangePassword(
    var current_password: String? = null,
    var new_password: String? = null
)

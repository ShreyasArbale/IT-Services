package com.example.maintenance

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.maintenance.databinding.ActivityProfilePageBinding
import com.example.maintenance.databinding.DialogConfirmLogoutBinding
import com.example.maintenance.login.LoginActivity
import com.example.maintenance.storage.SharedPreferenceManager

class ProfilePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePageBinding
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = SharedPreferenceManager.getInstance(applicationContext).getUser
        binding.tvUserName.text = user

        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.linearLayout4.setOnClickListener {
            showCustomDialogBox()
        }
        binding.linearLayout3.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showCustomDialogBox() {

        val customDialog = Dialog(this)
        val dialogBinding = DialogConfirmLogoutBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.show()

        dialogBinding.btnYes.setOnClickListener() {
            showCustomProgressDialog()
            if (SharedPreferenceManager.getInstance(this).isLoggedIn) {
                SharedPreferenceManager.getInstance(applicationContext).clear()
            }
            hideProgressDialog()
            customDialog.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialogBinding.btnNo.setOnClickListener() {
            customDialog.dismiss()
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
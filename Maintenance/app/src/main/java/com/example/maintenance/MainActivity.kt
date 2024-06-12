package com.example.maintenance

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.maintenance.databinding.ActivityMainBinding
import com.example.maintenance.databinding.DialogConfirmDeleteIssueBinding
import com.example.maintenance.databinding.DialogConfirmLogoutBinding
import com.example.maintenance.issue.HomeIssueActivity
import com.example.maintenance.maintenance.home_maintenance_activity.HomeMaintenanceActivity
import com.example.maintenance.login.LoginActivity
import com.example.maintenance.storage.SharedPreferenceManager
import com.example.maintenance.visit_maintenance.HomeVisitMaintenanceActivity
import com.example.maintenance.warranty_claim.HomeWarrantyClaimActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!SharedPreferenceManager.getInstance(this).isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.llMaintenance.setOnClickListener {
            val intent = Intent(this, HomeMaintenanceActivity::class.java)
            startActivity(intent)
        }

        binding.llVisitMaintenance.setOnClickListener {
            val intent = Intent(this, HomeVisitMaintenanceActivity::class.java)
            startActivity(intent)
        }

        binding.llWarrantyClaim.setOnClickListener {
            val intent = Intent(this, HomeWarrantyClaimActivity::class.java)
            startActivity(intent)
        }

        binding.llIssue.setOnClickListener {
            val intent = Intent(this, HomeIssueActivity::class.java)
            startActivity(intent)
        }

        binding.imageButtonMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }


        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout ->
                    showCustomDialogBox()

                R.id.profile ->
                    showProfilePage()
            }
            true
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

    private fun showProfilePage() {
        val intent = Intent(this, ProfilePageActivity::class.java)
        startActivity(intent)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
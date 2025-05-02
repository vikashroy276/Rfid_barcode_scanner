package com.mespl.emp_asset_mgmtapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.R.color.white
import com.mespl.emp_asset_mgmtapp.activities.login.LoginActivity
import com.mespl.emp_asset_mgmtapp.utils.CacheUtils
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.utils.NetworkUtils
import com.mespl.emp_asset_mgmtapp.databinding.ActivitySettingBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        if (!NetworkUtils.isNetworkAvailable(
                this, "Please check your internet connection and try again.")) {
            DialogManager.showErrorDialog(
                this@SettingActivity, "Please check your connection\n and try again."
            )
        }
        if (CacheUtils.getBASEURL().isNullOrEmpty()) {
            binding.ipaddresstxt.setText("http://")
        } else {
            binding.ipaddresstxt.setText(CacheUtils.getBASEURL())
        }

        binding.SubmitBtn.setOnClickListener {
            val url: String = binding.ipaddresstxt.text.toString()
            if (url != "") {
                CacheUtils.saveBASEURL(url)
                ApiClient.refreshRetrofit()
                Toast.makeText(this, "Saved Successfully !!", Toast.LENGTH_LONG).show()
                val intent = Intent(this@SettingActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Setting"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(resources.getColor(white))
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(resources.getColor(white))
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
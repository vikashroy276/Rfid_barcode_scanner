package com.mespl.emp_asset_mgmtapp.activities.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.R.color.white
import com.mespl.emp_asset_mgmtapp.activities.asset.AssetListActivity
import com.mespl.emp_asset_mgmtapp.activities.dashboard.assetcount.AssetCountResponse
import com.mespl.emp_asset_mgmtapp.activities.dashboard.employeecount.EmployeeCountResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.EmployeeListActivity
import com.mespl.emp_asset_mgmtapp.databinding.ActivityDashboardBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.scanner.BarcodeScanner
import com.mespl.emp_asset_mgmtapp.scanner.ScannerBaseActivityforcam
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*This is Dashboard Activity where we are showing Numbers of Total Employee and Totals Assets
* And Doing Rfid Connection work on click of connect button on Dashboard */

class DashboardActivity : ScannerBaseActivityforcam() {
    lateinit var binding: ActivityDashboardBinding
    private var isProcessingClick = false
    private var isConnected = true

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupalllistenersandAPI()
        setupToolbar()
        enableScan()

        binding.imgLogout.setOnClickListener {
            Log.e("Print", "Clicked ")
            if (!isProcessingClick) {
                isProcessingClick = true
                binding.imgLogout.isEnabled = false
                NoInternetConnectionDialog.showDialogLogout(
                    this, "Are you sure you want to logout?"
                )
                startLogoutCooldown()
            }
        }
        binding.connectBtn.setOnClickListener {
            connectRfid()
            if (isConnected) {
                // Change button color after connecting
                binding.connectBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.greencolor))
                // Optional: Change text to indicate connection
                binding.connectBtn.text = "Connected"
            } else {
                // Handle connection failure (optional)
                binding.connectBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary))
                binding.connectBtn.text = "Retry"
            }

        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {

    }

    override fun handleSetText(msg: String?) {

    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Dashboard"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            ContextCompat.getColor(this, white))
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            ContextCompat.getColor(
                this, white
            )
        )

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun showStatus(): Boolean {
        return super.showStatus()
    }

    override fun onScannedData(data: String?) {
        println("data 123edse $data")
    }

    /* Created by Kalpana Yadav on 15/10/2024
          * all click listeners & Count APIs*/
    private fun setupalllistenersandAPI() {
        callEmployeeCountAPI()
        callAssetCountAPI()
        binding.employeeCard.setOnClickListener {
            val i = Intent(this@DashboardActivity, EmployeeListActivity::class.java)
            startActivity(i)
        }
        binding.assetCard.setOnClickListener {
            val i = Intent(this@DashboardActivity, AssetListActivity::class.java)
            startActivity(i)
        }
    }

    private fun startLogoutCooldown() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isProcessingClick = false
                binding.imgLogout.isEnabled = true
            }
        }.start()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    /* Created by Vikash Roy on 15/10/2024
      * callEmployeeCountAPI for showing all employee list count API*/
    fun callEmployeeCountAPI() {
        try {
            ApiClient.client?.callemployeeCountAPI()
                ?.enqueue(object : Callback<EmployeeCountResponse> {
                    override fun onResponse(
                        call: Call<EmployeeCountResponse>, response: Response<EmployeeCountResponse>
                    ) {
                        val employeecountResponse = response.body()
                        if (employeecountResponse != null) {
                            if (employeecountResponse.Status == false) {
                                binding.empcounttxt.text =
                                    "(" + employeecountResponse.TotalCount.toString() + ")"
                            } else {
                                binding.empcounttxt.text =
                                    "(" + employeecountResponse?.TotalCount.toString() + ")"
                            }
                        } else {
                            binding.empcounttxt.text = "(0)"
                        }
                    }

                    override fun onFailure(
                        call: Call<EmployeeCountResponse>, t: Throwable
                    ) {
                        DialogManager.showErrorDialog(
                            this@DashboardActivity, t.message.toString()
                        )
                    }
                })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@DashboardActivity, e.message.toString())
        }
    }

    /* Created by Vikash Roy on 15/10/2024
  * callAssetCountAPI for showing all Asset list count API*/
    fun callAssetCountAPI() {
        try {
            ApiClient.client?.callassetCountAPI()?.enqueue(object : Callback<AssetCountResponse> {
                override fun onResponse(
                    call: Call<AssetCountResponse>, response: Response<AssetCountResponse>
                ) {
                    val assetcountResponse = response.body()
                    if (assetcountResponse != null) {
                        if (assetcountResponse.Status == false) {
                            binding.assetcounttxt.text =
                                "(" + assetcountResponse.TotalCount.toString() + ")"
                        } else {
                            binding.assetcounttxt.text =
                                "(" + assetcountResponse?.TotalCount.toString() + ")"
                        }
                    } else {
                        binding.assetcounttxt.text = "(0)"
                    }
                }

                override fun onFailure(call: Call<AssetCountResponse>, t: Throwable) {
                    DialogManager.showErrorDialog(
                        this@DashboardActivity, t.message.toString()
                    )
                }
            })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@DashboardActivity, e.message.toString())
        }
    }

    //this Function is written for
    override fun onDestroy() {
        super.onDestroy()
        if (rfidHandler != null && rfidHandler.isReaderConnected) {
            disconnectRfid()
        }
        disableScan()
        if (isFeatureAvailable("com.symbol.emdk")) {
            BarcodeScanner.deInitScanner()
            BarcodeScanner.releaseEmdk()
        }
        Log.e("Disconnect", "RFID Reader disconnected.")
    }
}
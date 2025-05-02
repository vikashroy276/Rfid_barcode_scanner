package com.mespl.emp_asset_mgmtapp.activities.employee

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.Asset
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.MapUnmapAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.MapUnmapAssetResponse
import com.mespl.emp_asset_mgmtapp.databinding.ActivityUnTaggedAssetsBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnTaggedAssetsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnTaggedAssetsBinding
    private var assetsName: String? = null
    private var serialNo: String? = null
    private var tagType: String? = null
    private var empID: String? = null
    private var empName: String? = null
    private lateinit var progressDialog: ProgressDialog
    private var isProcessingClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnTaggedAssetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        empName = intent.getStringExtra("EMPLOYEE_NAME")
        assetsName = intent.getStringExtra("assetName")
        serialNo = intent.getStringExtra("serialNumber")
        tagType = intent.getStringExtra("tagType")
        empID = intent.getStringExtra("EMPLOYEE_CODE")

        binding.empName.text = empName
        binding.assetsName.text = assetsName
        binding.srlNo.text = serialNo
        binding.tagType.text = tagType
        binding.empCode.text = empID

        // Initialize ProgressDialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Unmapping asset, please wait...")
            setCancelable(false)
        }

        binding.btnUnTagged.setOnClickListener {
            callMapUnmapAssetAPI()
        }

        /*This is Logout Button code for logout from the app
        * this is ImageView Button code for logout from the app  */
        binding.imgLogout.setOnClickListener {
            if (!isProcessingClick) {
                isProcessingClick = true
                binding.imgLogout.isEnabled = false
                NoInternetConnectionDialog.showDialogLogout(
                    this, "Are you sure you want to logout?"
                )
                startLogoutCooldown()
            }
        }
    }

    /*This Function is creating for logout button code and give one second delay time on Logout Button */
    private fun startLogoutCooldown() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isProcessingClick = false
                binding.imgLogout.isEnabled = true
            }
        }.start()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "UNMAP Asset"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .setTitleTextColor(resources.getColor(R.color.white))

        // Set the back arrow (navigation icon) color to black
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .navigationIcon?.setTint(resources.getColor(R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun callMapUnmapAssetAPI() {
        // Show the ProgressDialog when API call is made
        progressDialog.show()

        val mapUnmapAssetRequest = MapUnmapAssetRequest().apply {
            Action = "unmap"
            EmployeeId = empID
            Assets = listOf(Asset().apply {
                Id = serialNo
            })
            MappedBy = empID
            Remarks = "Testing by Vikash Roy"
            Log.d("TAG", "callmapUnmapAssetAPI: $Action, $EmployeeId, $Assets, $MappedBy, $Remarks")
        }
        try {
            ApiClient.client?.callmapUnmapAssetAPI(mapUnmapAssetRequest)
                ?.enqueue(object : Callback<MapUnmapAssetResponse> {
                    override fun onResponse(
                        call: Call<MapUnmapAssetResponse>, response: Response<MapUnmapAssetResponse>
                    ) {
                        progressDialog.dismiss()

                        val mapUnmapAssetResponse = response.body()
                        if (mapUnmapAssetResponse != null) {
                            if (mapUnmapAssetResponse.status == false) {
                                showDialog("Error", mapUnmapAssetResponse.Response.toString())
                                Log.e(
                                    "TAG",
                                    "onResponse: ${mapUnmapAssetResponse.Response.toString()}"
                                )
                            } else {
                                showDialog(
                                    "Success",
                                    "Employee successfully unmapped from specified assets."
                                )
                                Log.e(
                                    "TAG",
                                    "onResponse: ${mapUnmapAssetResponse.Response.toString()}"
                                )
                            }
                        }
                    }


                    override fun onFailure(call: Call<MapUnmapAssetResponse>, t: Throwable) {
                        // Dismiss the ProgressDialog on failure
                        progressDialog.dismiss()
                        showDialog("Error", t.message.toString())
                    }
                })
        } catch (e: Exception) {
            // Dismiss the ProgressDialog if there's an exception
            progressDialog.dismiss()
            showDialog("Error", e.message.toString())
        }
    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
//                finishActivity()
            }
            setCancelable(false)  // Prevent dismissal by tapping outside
        }
        builder.show()
    }

//    private fun finishActivity() {
//        val intent = Intent(this, AssetsItemActivity::class.java).apply {
//            putExtra("assetName", assetsName)
//            putExtra("serialNumber", serialNo)
//            putExtra("tagType", tagType)
//            putExtra("EMPLOYEE_CODE", empID)
//            putExtra("EMPLOYEE_NAME", empName)
//        }
//        startActivity(intent)
//        finish()
//    }

}


package com.mespl.emp_asset_mgmtapp.activities.mappingScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.AssetsDetailsResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.AssetsItemActivity
import com.mespl.emp_asset_mgmtapp.activities.employee.EmployeeActivity
import com.mespl.emp_asset_mgmtapp.activities.employee.EmployeeListActivity
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.taggedemployeelist.TaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.Asset
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.MapUnmapAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.MapUnmapAssetResponse
import com.mespl.emp_asset_mgmtapp.adapters.AssetDetailAdapter
import com.mespl.emp_asset_mgmtapp.databinding.ActivityMappingBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.scanner.ScannerBaseActivityforcam
import com.mespl.emp_asset_mgmtapp.utils.CacheUtils
import com.mespl.emp_asset_mgmtapp.utils.Converter
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import com.zebra.rfid.api3.TagData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class MappingActivity : ScannerBaseActivityforcam() {
    lateinit var binding: ActivityMappingBinding
    var handler: Handler? = null
    var r: Runnable? = null
    lateinit var EMPID: String
    private var sNoList: ArrayList<Asset> = ArrayList()
    private var isProcessingClick = false
    private lateinit var assetDetailAdapter: AssetDetailAdapter
    private var tagNo: String? = null
    private var empName: String? = null
    private var contact: String? = null
    private var designation: String? = null
    private var department: String? = null
    private var assetName: String? = null
    private var serialNumber: String? = null
    private var tagType: String? = null
    private var isProcessingTag = false
    private var debounceTime = 500L
    var employeetagged: TaggedEmployeeListResponse.TaggedEmployee? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMappingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        assetDetailAdapter = AssetDetailAdapter()

        EMPID = intent.getStringExtra("empid").toString()
        tagNo = intent.getStringExtra("tagNo")
        empName = intent.getStringExtra("EMPLOYEE_NAME")
        serialNumber = intent.getStringExtra("serialNumber")
        tagType = intent.getStringExtra("tagType")
        assetName = intent.getStringExtra("assetName")

        binding.mapBtn.visibility = View.GONE

        binding.mapBtn.setOnClickListener {
            callmapUnmapAssetAPI()
        }
        employeetagged =
            intent.getSerializableExtra("employee") as? TaggedEmployeeListResponse.TaggedEmployee

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

    override fun onResume() {
        super.onResume()
        val employeeid = EMPID
        if (employeeid.isNotEmpty()) {
            getEmplyeedetailAPI(employeeid)
        } else {
            Log.e("MappingActivity", "Employee ID is empty or invalid")

            DialogManager.showErrorDialog(this, "Invalid Employee ID")
        }
    }

    private fun backFun(){
        if (tagNo?.toIntOrNull() == 1) {
            if (employeetagged != null) {
                val intent = Intent(this@MappingActivity, EmployeeActivity::class.java).apply {
                    putExtra("employee", employeetagged as Serializable)
                }
                startActivity(intent)
                finish()
            }

        } else if (tagNo?.toIntOrNull() == 0) {
            val intent = Intent(this@MappingActivity, AssetsItemActivity::class.java)
            startActivity(intent)
            finishActivity()
            finish()
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
        supportActionBar?.title = "Mapping"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            resources.getColor(R.color.white))
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            resources.getColor(R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            backFun()
        }
    }

    override fun onScannedData(data: String?) {
        println("data ${data.toString()}")
        data?.let { it ->
            val existingText = binding.txtScanData.text.toString()
            val updatedText = if (existingText.isNotEmpty()) {
                "$existingText, $it"
            } else {
                it // For the first scan, set the text directly
            }
            binding.txtScanData.text = updatedText
            binding.scanBtn.setCardBackgroundColor(getColor(R.color.white))
            binding.txtscan.setTextColor(getColor(R.color.colorprimary))

            val asset = Asset().apply {
                Id = data
            }
            val isAssetAlreadyScanned = sNoList.any { it.Id == data }
            if (!isAssetAlreadyScanned) {
                sNoList.add(asset)
            }
            if (sNoList.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.adapter = assetDetailAdapter
                callAssetdetailAPI(it)
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.mapBtn.visibility = View.GONE
            }
        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {

    }

    fun handleTagdata(tagData: String) {
        println("tagData2345: $tagData")
        if (isProcessingTag) {
            return
        }
        tagData.let { it ->
            if (tagData.isNotEmpty()) {
                isProcessingTag = true
                val sb = StringBuilder()
                sb.append(tagData)
                val tmpTagData = Converter.hexaToString(sb.toString())
                    .replace("\u0000".toRegex(), "") // Remove null characters
                    .trim()
                runOnUiThread {
                    val asset = Asset().apply {
                        Id = tmpTagData
                    }
                    val isAssetAlreadyScanned = sNoList.any { it.Id == tmpTagData }
                    if (!isAssetAlreadyScanned) {
                        sNoList.add(asset)
                    }
                    if (sNoList.isNotEmpty()) {
                        binding.txtScanData.append(tmpTagData)
                        callAssetdetailAPI(binding.txtScanData.text.toString().trim())
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.recyclerView.adapter = assetDetailAdapter
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        binding.mapBtn.visibility = View.GONE
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    isProcessingTag = false
                }, debounceTime)
            } else {
                runOnUiThread {
                    binding.txtScanData.text = ""
                    binding.txtScanData.append("Please scan again\n")
                }
            }
        }
    }

    override fun handleSetText(msg: String?) {
          
    }


    private fun callmapUnmapAssetAPI() {
        val mapUnmapAssetRequest = MapUnmapAssetRequest().apply {
            Action = "map"
            EmployeeId = EMPID
            Assets = sNoList
            MappedBy = CacheUtils.getUserId(this@MappingActivity)
            Remarks = "Testing by Vikash Roy"
        }
        try {
            ApiClient.client?.callmapUnmapAssetAPI(mapUnmapAssetRequest)?.enqueue(object :
                Callback<MapUnmapAssetResponse> {
                override fun onResponse(
                    call: Call<MapUnmapAssetResponse>,
                    response: Response<MapUnmapAssetResponse>
                ) {
                    val mapUnmapAssetResponse = response.body()
                    if (mapUnmapAssetResponse != null) {
                        if (mapUnmapAssetResponse.status == false) {
                            DialogManager.showErrorDialog(
                                this@MappingActivity,
                               "Employee is already mapped for this asset"
                            )
                        } else {
                            DialogManager.showSuccessDialog(
                                this@MappingActivity, "Asset mapped Successfully for this Employee",
                                EmployeeListActivity::class.java
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<MapUnmapAssetResponse>, t: Throwable) {
                    DialogManager.showErrorDialog(this@MappingActivity, "Invalid Asset Code")
                }
            })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@MappingActivity, e.message.toString())
        }
    }


    private fun callAssetdetailAPI(assetid: String) {
        val getAssetDetailRequest = GetAssetDetailRequest().apply {
            AssetMasterId = assetid
        }
        try {
            ApiClient.client?.callAssetDetailAPI(getAssetDetailRequest)?.enqueue(object :
                Callback<GetAssetDetailResponse> {
                override fun onResponse(
                    call: Call<GetAssetDetailResponse>,
                    response: Response<GetAssetDetailResponse>
                ) {
                    binding.mapBtn.visibility = View.VISIBLE
                    val assetdetailResponse = response.body()
                    if (assetdetailResponse != null) {
                        if (assetdetailResponse.status == false) {
                            DialogManager.showErrorDialog(
                                this@MappingActivity,
                                "No data found with this employee code."
                            )
                        } else {
                            assetdetailResponse.details?.let { details ->
                                val serialNumber = details.serialNumber
                                val assetName = details.assetName

                                // Add the asset detail to the adapter's list and update the RecyclerView
                                val assetDetail = AssetsDetailsResponse(
                                    serialNumber = serialNumber.toString(),
                                    assetName = assetName.toString()
                                )
                                assetDetailAdapter.addAssetDetail(assetDetail)
                                assetDetailAdapter.notifyDataSetChanged()

                                Log.d(
                                    "AssetDetail",
                                    "Serial Number: $serialNumber, Asset Name: $assetName"
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetAssetDetailResponse>, t: Throwable) {
                    DialogManager.showErrorDialog(this@MappingActivity, "Invalid Employee Code")
                }
            })
        } catch (e: Exception) {
            Log.e("Asset", "API call failed: ${e.message}")
            DialogManager.showErrorDialog(this@MappingActivity, e.message.toString())
        }
    }

    fun getEmplyeedetailAPI(employeeid: String) {
        val employeeDetailRequest = GetEmployeeDetailRequest().apply {
            EmployeeCode = employeeid
        }

        try {
            ApiClient.client?.callEmployeedetailAPI(employeeDetailRequest)?.enqueue(object :
                Callback<GetEmployeeDetailResponse> {
                override fun onResponse(
                    call: Call<GetEmployeeDetailResponse>,
                    response: Response<GetEmployeeDetailResponse>
                ) {
                    val employeedetailResponse = response.body()
                    if (employeedetailResponse != null) {
                        if (employeedetailResponse.status == false) {
                            // Show error dialog if no data found
                            DialogManager.showErrorDialog(
                                this@MappingActivity,
                                "No data found with this employee code."
                            )
                        } else {
                            // Assuming the employee details are in the response body
                            val employee = employeedetailResponse.details

                            contact = employee?.contactNo
                            designation = employee?.designation
                            department = employee?.department

                            println("Lists of Employee : $contact $designation $department")


                        }
                    }
                }

                override fun onFailure(call: Call<GetEmployeeDetailResponse>, t: Throwable) {
                    // Show error dialog on failure
                    DialogManager.showErrorDialog(
                        this@MappingActivity,
                        t.message.toString()
                    )
                    Log.e("EmployeeActivity", "API call failed: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("EmployeeActivity", "API call failed: ${e.message}")
            DialogManager.showErrorDialog(this@MappingActivity, e.message.toString())
        }
    }

    private fun finishActivity() {
        val intent = Intent(this, AssetsItemActivity::class.java).apply {
            putExtra("assetName", assetName)
            putExtra("serialNumber", serialNumber)
            putExtra("tagType", tagType)
            putExtra("EMPLOYEE_CODE", EMPID)
            putExtra("EMPLOYEE_NAME", empName)
        }
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (tagNo?.toIntOrNull() == 1) {
            // Check if employeetagged is not null before accessing its fields
            employeetagged?.let { employee ->
                val intent = Intent(this, EmployeeActivity::class.java).apply {
                    putExtra("employee", employee as Serializable)
                }
                startActivity(intent)
                finish()
            }
        } else if (tagNo?.toIntOrNull() == 0) {
            val intent = Intent(this@MappingActivity, AssetsItemActivity::class.java)
            startActivity(intent)
            finishActivity()
            finish()
        }
    }
}
package com.mespl.emp_asset_mgmtapp.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee.TagDetagEmployeeRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee.TagDetagEmployeeResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.taggedemployeelist.TaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.MappingActivity
import com.mespl.emp_asset_mgmtapp.databinding.ActivityEmployeeBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.scanner.ScannerBaseActivityforcam
import com.mespl.emp_asset_mgmtapp.utils.CacheUtils
import com.mespl.emp_asset_mgmtapp.utils.Converter
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.utils.Helper
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import com.zebra.rfid.api3.TagData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class EmployeeActivity : ScannerBaseActivityforcam() {
    lateinit var binding: ActivityEmployeeBinding
    var r: Runnable? = null
    var employeetagged: TaggedEmployeeListResponse.TaggedEmployee? = null
    lateinit var EMPID: String
    private var selectedTagType: String? = null
    private var isProcessingClick = false
    private var isProcessingTag = false
    private var debounceTime = 500L
    var tagValues :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        initUi()

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

    // Setting up Toolbar
    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Tagged Employee"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            resources.getColor(
                R.color.white
            )
        )

        // Set the back arrow (navigation icon) color to black
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            resources.getColor(R.color.white)
        )
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initUi() {
        employeetagged =
            intent.getSerializableExtra("employee") as? TaggedEmployeeListResponse.TaggedEmployee

        employeetagged?.let { employee ->
            binding.txtempcode.text = employee.employeeCode.toString()
            binding.txtempname.text = "${employee.fName} ${employee.lName}"
            binding.txtcontactno.text = employee.contactNo
            binding.txtempdesignation.text = employee.designation
            binding.txtempdepartment.text =
                employee.role // Fixed to use 'role' instead of 'designation'
            val formattedDate = Helper.convertTimestampToDateFormat(employee.createdAt.toString())
            binding.txtempjoingdate.text = formattedDate
            binding.txttagtype.text = employee.tagType
            EMPID = employee.employeeCode.toString()

        }

        binding.textMapAssets.setOnClickListener {
            val i = Intent(this@EmployeeActivity, MappingActivity::class.java)
            val tagNo = "1"
            i.putExtra("tagNo", tagNo)
            i.putExtra("empid", EMPID)
            i.putExtra("EMPLOYEE_NAME", "${employeetagged?.fName} ${employeetagged?.lName}")
            i.putExtra("employee", employeetagged as Serializable)
            startActivity(i)
            finish()
        }
    }

    override fun onScannedData(data: String?) {
        binding.scandatatxt.text = data
        selectedTagType = "Barcode"
        getEmplyeedetailAPI(data.toString())
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {

    }

    fun getEmplyeedetailAPI(employeeid: String) {
        val employeeDetailRequest = GetEmployeeDetailRequest().apply {
            EmployeeCode = employeeid
        }
        try {
            ApiClient.client?.callEmployeedetailAPI(employeeDetailRequest)
                ?.enqueue(object : Callback<GetEmployeeDetailResponse> {
                    override fun onResponse(
                        call: Call<GetEmployeeDetailResponse>,
                        response: Response<GetEmployeeDetailResponse>
                    ) {
                        val employeedetailResponse = response.body()
                        if (employeedetailResponse != null) {
                            if (employeedetailResponse.status == false) {
                                binding.employeeDetailCardView.visibility = View.GONE
                                DialogManager.showErrorDialog(
                                    this@EmployeeActivity, "No data found with this employee code."
                                )
                            } else {
                                binding.employeeDetailCardView.visibility = View.VISIBLE
                                employeedetailResponse.details?.let { employeeDetail ->
                                    binding.scanempcodetxt.text = employeeDetail.employeeCode
                                    binding.scanempname.text =
                                        "${employeeDetail.fName} ${employeeDetail.lName}"
                                    binding.detagBtn.setOnClickListener {
                                        calltagDetagEmployeeAPI(
                                            employeeDetail.employeeCode,
                                            employeeDetail.isTagged,
                                            employeeDetail.tagType,
                                        )
                                    }
                                } ?: run {
                                    binding.employeeDetailCardView.visibility = View.GONE
                                    DialogManager.showErrorDialog(
                                        this@EmployeeActivity, "Details are null."
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetEmployeeDetailResponse>, t: Throwable) {
                        binding.employeeDetailCardView.visibility = View.GONE
                        DialogManager.showErrorDialog(
                            this@EmployeeActivity, "Invalid Employee Code"
                        )
                        Log.e("EmployeeActivity", "API call failed: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.e("EmployeeActivity", "API call failed: ${e.message}")
            DialogManager.showErrorDialog(this@EmployeeActivity, "Invalid Employee Code")
        }
    }

     fun handleTagdata(tagData: String) {
        if (isProcessingTag) {
            return
        }
        if (tagData.isNotEmpty()) {
            isProcessingTag = true
            val sb = StringBuilder()
            sb.append(tagData)
            tagValues = tagData
            println("Tag Values : $tagValues")
            val tmpTagData = Converter.hexaToString(sb.toString())
                .replace("\\u0000".toRegex(), "") // Remove null characters
                .trim()
            runOnUiThread {
                selectedTagType = "Rfid"
                binding.scandatatxt.append(tmpTagData)
                getEmplyeedetailAPI(binding.scandatatxt.text.toString().trim())
            }
            Handler(Looper.getMainLooper()).postDelayed({
                isProcessingTag = false
            }, debounceTime)
        } else {
            runOnUiThread {
                binding.scandatatxt.text = ""
                binding.scandatatxt.append("Please scan again\n")
            }
        }
    }

    override fun handleSetText(msg: String?) {

    }

    fun calltagDetagEmployeeAPI(employeeCode: String?, istagged: Boolean?, tagType: String?) {
        val tagValues = if (selectedTagType.equals("RFID", ignoreCase = true)) tagValues else " "
        val tagDetagEmployeeRequest = TagDetagEmployeeRequest().apply {
            action = "D"
            EmployeeCode = employeeCode
            IsTagged = istagged
            TagType = selectedTagType
            TaggedBy = CacheUtils.getUserId(this@EmployeeActivity)
            TagId =  tagValues
            TagValue = binding.scandatatxt.text.toString()
        }
        try {
            ApiClient.client?.calltagDetagEmployeeAPI(tagDetagEmployeeRequest)
                ?.enqueue(object : Callback<TagDetagEmployeeResponse> {
                    override fun onResponse(
                        call: Call<TagDetagEmployeeResponse>,
                        response: Response<TagDetagEmployeeResponse>
                    ) {
                        val tagdetagResponse = response.body()
                        if (tagdetagResponse != null) {
                            if (tagdetagResponse.status == false) {
                                DialogManager.showErrorDialog(
                                    this@EmployeeActivity, tagdetagResponse.response.toString()
                                )
                            } else {
                                DialogManager.showSuccessDialog(
                                    this@EmployeeActivity,
                                    tagdetagResponse.response.toString(),
                                    EmployeeListActivity::class.java
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<TagDetagEmployeeResponse>, t: Throwable) {
                        DialogManager.showErrorDialog(
                            this@EmployeeActivity, "Invalid Employee Code"
                        )
                    }
                })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@EmployeeActivity, "Invalid Employee Code")
        }
    }
}


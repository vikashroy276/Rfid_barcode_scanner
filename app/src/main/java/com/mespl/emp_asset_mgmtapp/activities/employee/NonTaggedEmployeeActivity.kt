package com.mespl.emp_asset_mgmtapp.activities.employee

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.nontaggedtmployeelist.NonTaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee.TagDetagEmployeeRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee.TagDetagEmployeeResponse
import com.mespl.emp_asset_mgmtapp.databinding.ActivityNonTaggedEmployeeBinding
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

class NonTaggedEmployeeActivity : ScannerBaseActivityforcam() {
    lateinit var binding: ActivityNonTaggedEmployeeBinding
    private var handler: Handler? = null
    var r: Runnable? = null
    private var nonemployeetagged: NonTaggedEmployeeListResponse.NonTaggedEmployee? =
        null // Declare employeetagged as nullable
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 1001
    private var selectedTagType: String? = null
    private var isProcessingClick = false
    private var isProcessingTag = false
    private var debounceTime = 600L
    var tagValues :String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonTaggedEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        initUi()

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
        supportActionBar?.title = "Non-Tag Employee "
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
        // Retrieve the TaggedEmployee object from intent
        nonemployeetagged =
            intent.getSerializableExtra("employee") as? NonTaggedEmployeeListResponse.NonTaggedEmployee

        // Check if employeetagged is not null before accessing its fields
        nonemployeetagged?.let { employee ->
            binding.txtempcode.text = employee.employeeCode.toString()
            binding.txtempname.text = "${employee.fName} ${employee.lName}"
            binding.txtcontactno.text = employee.contactNo
            binding.txtempdesignation.text = employee.designation
            binding.txtempdepartment.text = employee.role
            val formattedDate = Helper.convertTimestampToDateFormat(employee.createdAt.toString())
            binding.txtempjoingdate.text = formattedDate
        }

    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        // Handle barcode events
    }

    override fun onScannedData(data: String?) {
        data?.let {
            selectedTagType = "Barcode"
            getEmplyeedetailAPI(it)
        }
        binding.scandatatxt.text = data
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
                                    this@NonTaggedEmployeeActivity,
                                    "No data found with this employee code."
                                )
                            } else {
                                binding.employeeDetailCardView.visibility = View.VISIBLE
                                employeedetailResponse.details?.let { employeeDetail ->
                                    binding.scanempcodetxt.text = employeeDetail.employeeCode
                                    binding.scanempname.text =
                                        "${employeeDetail.fName} ${employeeDetail.lName}"
                                    binding.tagBtn.setOnClickListener {
                                        calltagDetagEmployeeAPI(
                                            employeeDetail.employeeCode,
                                            employeeDetail.isTagged,
                                            employeeDetail.tagType,
                                        )
                                    }
                                } ?: run {
                                    binding.employeeDetailCardView.visibility = View.GONE
                                    DialogManager.showErrorDialog(
                                        this@NonTaggedEmployeeActivity, "Details are null."
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetEmployeeDetailResponse>, t: Throwable) {
                        binding.employeeDetailCardView.visibility = View.GONE
                        DialogManager.showErrorDialog(
                            this@NonTaggedEmployeeActivity, "Invalid Employee Code"
                        )
                        Log.e("EmployeeActivity", "API call failed: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.e("EmployeeActivity", "API call failed: ${e.message}")
            DialogManager.showErrorDialog(this@NonTaggedEmployeeActivity, "Invalid Employee Code")
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
            action = "T"
            EmployeeCode = employeeCode
            IsTagged = istagged
            TagType = selectedTagType
            TagId = tagValues
            TagValue = binding.scandatatxt.text.toString()
            TaggedBy = CacheUtils.getUserId(this@NonTaggedEmployeeActivity)

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
                                    this@NonTaggedEmployeeActivity,
                                    tagdetagResponse.response.toString()
                                )
                            } else {
                                DialogManager.showSuccessDialog(
                                    this@NonTaggedEmployeeActivity,
                                    tagdetagResponse.response.toString(),
                                    EmployeeListActivity::class.java
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<TagDetagEmployeeResponse>, t: Throwable) {
                        DialogManager.showErrorDialog(
                            this@NonTaggedEmployeeActivity, "Invalid Employee Code"
                        )
                    }
                })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@NonTaggedEmployeeActivity, "Invalid Employee Code")
        }
    }
}


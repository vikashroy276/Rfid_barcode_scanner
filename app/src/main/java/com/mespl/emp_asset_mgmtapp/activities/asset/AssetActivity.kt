package com.mespl.emp_asset_mgmtapp.activities.asset

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.R.color.white
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.gettaggedassetlist.TaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset.TagDetagAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset.TagDetagAssetResponse
import com.mespl.emp_asset_mgmtapp.databinding.ActivityAssetBinding
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

class AssetActivity : ScannerBaseActivityforcam() {
    lateinit var binding: ActivityAssetBinding
    var assetlist: TaggedAssetListResponse.TaggedAsset? = null
    var handler: Handler? = null
    var r: Runnable? = null
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 100
    private var isProcessingClick = false
    private var isProcessingTag = false
    private var debounceTime = 500L
    private var selectedTagType: String? = null
    var tagValues :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        assetlist = intent.getSerializableExtra("assetlist") as? TaggedAssetListResponse.TaggedAsset

        assetlist?.let { assets ->
            binding.assetidtxt.text = assets.SerialNumber
            binding.assetnametxt.text = assets.AssetName
            binding.assetcategorytxt.text = assets.Category
            binding.assetstatustxt.text = assets.IsActive.toString()

        }

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
        supportActionBar?.title = "Tag-Asset"

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            resources.getColor(white))
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            resources.getColor(white)
        )
       binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {}

    override fun onScannedData(data: String?) {
        data?.let {
            selectedTagType = "Barcode"
            callAssetdetailAPI(it)
        }
        binding.scanassetdatatxt.text = data
    }

    fun callAssetdetailAPI(assetid: String) {
        val getAssetDetailRequest = GetAssetDetailRequest().apply {
            AssetMasterId = assetid
        }
        try {
            ApiClient.client?.callAssetDetailAPI(getAssetDetailRequest)
                ?.enqueue(object : Callback<GetAssetDetailResponse> {
                    override fun onResponse(
                        call: Call<GetAssetDetailResponse>,
                        response: Response<GetAssetDetailResponse>
                    ) {
                        val assetdetailResponse = response.body()
                        if (assetdetailResponse != null) {
                            if (assetdetailResponse.status == false) {
                                binding.assetDetailCardView.visibility = View.GONE
                                DialogManager.showErrorDialog(
                                    this@AssetActivity, "No data found with this employee code."
                                )
                            } else {
                                binding.assetDetailCardView.visibility = View.VISIBLE
                                assetdetailResponse.details?.let { assetDetail ->
                                    binding.scanassetidtxt.text = assetDetail.serialNumber
                                    binding.scanassetname.text = assetDetail.assetName
                                    binding.detagBtn.setOnClickListener {

                                        calltagDetagAssetAPI(
                                            assetDetail.serialNumber,
                                            assetDetail.isTagged,
                                            assetDetail.TagType,
                                        )
                                    }
                                } ?: run {
                                    binding.assetDetailCardView.visibility = View.GONE
                                    DialogManager.showErrorDialog(
                                        this@AssetActivity, "Details are null."
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetAssetDetailResponse>, t: Throwable) {
                        binding.assetDetailCardView.visibility = View.GONE
                        DialogManager.showErrorDialog(this@AssetActivity, "Invalid Asset Code")
                        Log.e("Aseet", "API call failed: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.e("Asset", "API call failed: ${e.message}")
            DialogManager.showErrorDialog(this@AssetActivity, "Invalid Asset Code")
        }
    }

    fun calltagDetagAssetAPI(serialNumber: String?, istagged: Boolean?, tagType: String?) {
        val tagValues = if (selectedTagType.equals("RFID", ignoreCase = true)) tagValues else " "
        val tagDetagAssetRequest = TagDetagAssetRequest().apply {
            action = "D"
            SerialNumber = serialNumber
            IsTagged = istagged
            TagType = selectedTagType
            TagId = tagValues
            TagValue = binding.scanassetdatatxt.text.toString()
            TaggedBy = CacheUtils.getUserId(this@AssetActivity)
        }
        try {
            ApiClient.client?.calltagDetagAssetAPI(tagDetagAssetRequest)
                ?.enqueue(object : Callback<TagDetagAssetResponse> {
                    override fun onResponse(
                        call: Call<TagDetagAssetResponse>, response: Response<TagDetagAssetResponse>
                    ) {
                        val tagdetagResponse = response.body()
                        if (tagdetagResponse != null) {
                            if (tagdetagResponse.status == false) {
                                DialogManager.showErrorDialog(
                                    this@AssetActivity, tagdetagResponse.response.toString()
                                )
                            } else {
                                DialogManager.showSuccessDialog(
                                    this@AssetActivity,
                                    tagdetagResponse.response.toString(),
                                    AssetListActivity::class.java
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<TagDetagAssetResponse>, t: Throwable) {
                        DialogManager.showErrorDialog(this@AssetActivity, "Invalid Asset Code")
                    }
                })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@AssetActivity, "Invalid Asset Code")
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
            tagValues = tagData.toString()
            val tmpTagData = Converter.hexaToString(sb.toString())
                .replace("\\u0000".toRegex(), "") // Remove null characters
                .trim()
            runOnUiThread {
                binding.scanassetdatatxt.append(tmpTagData)
                selectedTagType = "RFID"
                callAssetdetailAPI(binding.scanassetdatatxt.text.toString())

            }
            Handler(Looper.getMainLooper()).postDelayed({
                isProcessingTag = false
            }, debounceTime)
        } else {
            runOnUiThread {
                binding.scanassetdatatxt.text = ""
                binding.scanassetdatatxt.append("Please scan again\n")
            }
        }
    }

    override fun handleSetText(msg: String?) {
          
    }

}
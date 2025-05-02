package com.mespl.emp_asset_mgmtapp.activities.asset

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.getnontaggedassetlist.NonTaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset.TagDetagAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset.TagDetagAssetResponse
import com.mespl.emp_asset_mgmtapp.databinding.ActivityNonTaggedAssetBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.scanner.ScannerBaseActivityforcam
import com.mespl.emp_asset_mgmtapp.utils.CacheUtils
import com.mespl.emp_asset_mgmtapp.utils.Converter
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NonTaggedAssetActivity : ScannerBaseActivityforcam() {
    lateinit var binding: ActivityNonTaggedAssetBinding
    var nonassetlist: NonTaggedAssetListResponse.NonTaggedAsset? = null
    var r: Runnable? = null
    private var selectedTagType: String? = null
    private var isProcessingClick = false
    private var isProcessingTag = false
    private var debounceTime = 500L
    var tagValues: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonTaggedAssetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        nonassetlist =
            intent.getSerializableExtra("nonassetlist") as? NonTaggedAssetListResponse.NonTaggedAsset

        nonassetlist?.let { assets ->
            binding.nonassetidtxt.text = assets.SerialNumber
            binding.nonassetnametxt.text = assets.AssetName
            binding.nonassetcategorytxt.text = assets.Category
            binding.nonassetstatustxt.text = assets.IsActive.toString()
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

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Non-Tag-Asset"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            resources.getColor(R.color.white)
        )
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            resources.getColor(R.color.white)
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
        binding.nonscanassetdatatxt.text = data
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
            println("Processing of tagValues are :$tagValues")
            val tmpTagData = Converter.hexaToString(sb.toString())
                .replace("\\u0000".toRegex(), "") // Remove null characters
                .trim()
            runOnUiThread {
                binding.nonscanassetdatatxt.append(tmpTagData)
                selectedTagType = "Rfid"
                callAssetdetailAPI(binding.nonscanassetdatatxt.text.toString())

            }
            Handler(Looper.getMainLooper()).postDelayed({
                isProcessingTag = false
            }, debounceTime)
        } else {
            runOnUiThread {
                binding.nonscanassetdatatxt.text = ""
                binding.nonscanassetdatatxt.append("Please scan again\n")
            }
        }
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
                                binding.nonassetDetailCardView.visibility = View.GONE
                                DialogManager.showErrorDialog(
                                    this@NonTaggedAssetActivity,
                                    "No data found with this employee code."
                                )
                            } else {
                                binding.nonassetDetailCardView.visibility = View.VISIBLE
                                assetdetailResponse.details?.let { assetDetail ->
                                    binding.nonscanassetidtxt.text = assetDetail.serialNumber
                                    binding.nonscanassetname.text = assetDetail.assetName
                                    binding.nonassetcategorytxt.text = assetDetail.Category
                                    binding.nonassetstatustxt.text = assetDetail.isActive.toString()
                                    binding.detagBtn.setOnClickListener {

                                        calltagDetagAssetAPI(
                                            assetDetail.serialNumber,
                                            assetDetail.isTagged,
                                        )
                                    }
                                } ?: run {
                                    binding.nonassetDetailCardView.visibility = View.GONE
                                    DialogManager.showErrorDialog(
                                        this@NonTaggedAssetActivity, "Details are null."
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetAssetDetailResponse>, t: Throwable) {
                        binding.nonassetDetailCardView.visibility = View.GONE
                        DialogManager.showErrorDialog(
                            this@NonTaggedAssetActivity, t.message.toString()
                        )
                        Log.e("Asset", "API call failed: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.e("Asset", "API call failed: ${e.message}")
            DialogManager.showErrorDialog(this@NonTaggedAssetActivity, e.message.toString())
        }
    }

    fun calltagDetagAssetAPI(serialNumber: String?, istagged: Boolean?) {
        val tagValues = if (selectedTagType.equals("RFID", ignoreCase = true)) tagValues else " "
        val tagDetagAssetRequest = TagDetagAssetRequest().apply {
            action = "T"
            SerialNumber = serialNumber
            IsTagged = istagged
            TagType = selectedTagType
            TagId = tagValues
            TagValue = binding.nonscanassetdatatxt.text.toString()
            TaggedBy = CacheUtils.getUserId(this@NonTaggedAssetActivity)
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
                                    this@NonTaggedAssetActivity,
                                    tagdetagResponse.response.toString()
                                )
                            } else {
                                DialogManager.showSuccessDialog(
                                    this@NonTaggedAssetActivity,
                                    tagdetagResponse.response.toString(),
                                    AssetListActivity::class.java
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<TagDetagAssetResponse>, t: Throwable) {
                        DialogManager.showErrorDialog(
                            this@NonTaggedAssetActivity, "Invalid Employee Code"
                        )
                    }
                })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@NonTaggedAssetActivity, e.message.toString())
        }
    }

    override fun handleSetText(msg: String?) {

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
}
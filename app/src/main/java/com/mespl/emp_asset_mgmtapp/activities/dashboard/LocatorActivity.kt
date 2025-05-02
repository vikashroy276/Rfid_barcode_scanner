package com.mespl.emp_asset_mgmtapp.activities.dashboard

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.databinding.ActivityLocatorBinding
import com.mespl.emp_asset_mgmtapp.rfid.RFIDHandler
import com.mespl.emp_asset_mgmtapp.rfid.RFIDHandler.ResponseHandlerInterface
import com.mespl.emp_asset_mgmtapp.scanner.ScannerBaseActivityforcam
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import com.mespl.emp_asset_mgmtapp.utils.RangeGraph
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.TagData

class LocatorActivity : ScannerBaseActivityforcam(), ResponseHandlerInterface {
    private lateinit var binding: ActivityLocatorBinding
    private var ctx: Context? = null
    private var tagEPC: String? = null
    private var locationBar: RangeGraph? = null
    private var isProcessingClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ctx = this
        setupToolbar()
        logOut()
        tagEPC = intent.getStringExtra("tagId")
        println("Tagsd Id : $tagEPC")

        locationBar = binding.locationBar
        epc = tagEPC
        binding.tagEPC.text = tagEPC

    }

    override fun onResume() {
        super.onResume()
        rfidHandler = RFIDHandler.getInstance(30, this) // 30-second timeout
        if (rfidHandler != null) {
            rfidHandler.setHandler(this)
        } else {
            Log.e(TAG, "RFIDHandler instance is null!")
            handleSetText("RFID Initialization Failed")
        }
    }

    override fun onScannedData(data: String) {
    }

    fun logOut() {
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


    override fun handleTagdata(tagData: Array<TagData>) {
        for (tag in tagData) {
            val percent = tag.LocationInfo.relativeDistance.toInt()
            runOnUiThread {
                locationBar!!.value = percent
                locationBar!!.invalidate()
            }
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            rfidHandler.locate(epc) //reads and shows inventory
        } else {
            rfidHandler.stopInventory() //on release stops showing any new inventory
        }
    }

    override fun handleSetText(msg: String) {

    }

    override fun showStatus(): Boolean {
        return false
    }

    fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.title = "Locater"
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorprimary))
        if (toolbar.navigationIcon != null) {
            toolbar.navigationIcon!!.setTint(resources.getColor(R.color.white))
        }
        toolbar.setNavigationOnClickListener { v: View? ->
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rfidHandler != null) {
            try {
                rfidHandler.stopInventory()
                rfidHandler.stopLocate()
                rfidHandler.setHandler(null)
                //                rfidHandler.releaseResources();
            } catch (e: OperationFailureException) {
                Log.e(TAG, "Error during RFIDHandler cleanup: " + e.message, e)
            } catch (e: InvalidUsageException) {
                Log.e(TAG, "Error during RFIDHandler cleanup: " + e.message, e)
            }
        }
    }

    override fun onBarcodeEvent(barcodeReadEvent: BarcodeReadEvent) {
    }

    companion object {
        const val TAG: String = "LocatorActivity"
        var epc: String? = null
        var name: String? = null
    }
}

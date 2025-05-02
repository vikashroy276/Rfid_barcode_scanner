package com.mespl.emp_asset_mgmtapp.activities.employee

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.honeywell.aidc.BarcodeReadEvent
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.asset.ItemAsset.ItemAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.ItemAsset.ItemAssetResponse
import com.mespl.emp_asset_mgmtapp.activities.dashboard.LocatorActivity
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.MappingActivity
import com.mespl.emp_asset_mgmtapp.databinding.EmpitemBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.rfid.RFIDHandler
import com.mespl.emp_asset_mgmtapp.scanner.ScannerBaseActivityforcam
import com.mespl.emp_asset_mgmtapp.utils.NetworkUtils.isNetworkAvailable
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/*This class is created for show Tagged Assets data in table form and there after complete the task
* you can unmap the assets from the Employee and table
* This class is created By Vikash Roy 29/10/2024 */
class AssetsItemActivity : ScannerBaseActivityforcam(), RFIDHandler.ResponseHandlerInterface {
    private lateinit var binding: EmpitemBinding
    private var employeeName: String? = null
    private var employeeCode: String? = null
    private var isProcessingClick = false
    private var assetsName: String? = null
    private var serialNo: String? = null
    private var tagType: String? = null
    private var empID: String? = null
    private var empName: String? = null
    private var isProcessingTag = false
    private var tagValues: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EmpitemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        empName = intent.getStringExtra("EMPLOYEE_NAME")
        empID = intent.getStringExtra("EMPLOYEE_CODE")

        /*This is Logout Button code for logout from the app
        * this is ImageView Button code for logout from the app
        * Added this code by Vikash Roy 25/10/2024 */
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

        binding.mapAssetsBtn.setOnClickListener {
            val i = Intent(this@AssetsItemActivity, MappingActivity::class.java)
            val tagNo = "0"
            i.putExtra("tagNo", tagNo)
            i.putExtra("empid", employeeCode)
            i.putExtra("EMPLOYEE_NAME", employeeName)
            i.putExtra("assetName", assetsName)
            i.putExtra(("serialNumber"), serialNo)
            i.putExtra("tagType", tagType)
            startActivity(i)
        }
    }
//
//    //use to launch an activity
//    private fun launchScreen(i: Intent) {
//        i.putExtra("tagId", tagId)
//        startActivity(i)
//        overridePendingTransition(0, 0)
//    }

    // Setting up Toolbar
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Asset Item"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            resources.getColor(
                R.color.white
            )
        )
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            resources.getColor(R.color.white)
        )

        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, EmployeeListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        employeeName = intent.getStringExtra("EMPLOYEE_NAME")
        employeeCode = intent.getStringExtra("EMPLOYEE_CODE")
        assetsName = intent.getStringExtra("assetName")
        serialNo = intent.getStringExtra("serialNumber")
        tagType = intent.getStringExtra("tagType")

        binding.empName.text = employeeName
        binding.empCode.text = employeeCode

        if (!isNetworkAvailable(this, "Please check your internet connection and try again.")) {
        } else {
            callItemAssetsApi(employeeCode.toString())
        }
        // Initialize RFIDHandler
        rfidHandler = RFIDHandler.getInstance(30, this) // 30-second timeout
        if (rfidHandler != null) {
            rfidHandler.setHandler(this)
        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {

    }

    override fun onScannedData(data: String?) {

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
        }
        updateTagData(tagData)
    }

    override fun handleSetText(msg: String?) {

    }

    private fun updateTagData(tagData: String) {}


    /*This Function is creating for logout button code and give one second delay time on Logout Button
    * Added this code by Vikash Roy 25/10/2024 */
    private fun startLogoutCooldown() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isProcessingClick = false
                binding.imgLogout.isEnabled = true
            }
        }.start()
    }

    /*In this Api i'm getting Assets Item List and after getting
    these list i am bind them in table for get the Item assets i am sending
    employee code in request This code is created by Vikash Roy 29/10/2024*/
    private fun callItemAssetsApi(employeeCode: String) {
        val request = ItemAssetRequest(employeeCode)

        ApiClient.client?.callgetMappedAssetsByEmpCodeAPI(request)
            ?.enqueue(object : Callback<ItemAssetResponse?> {
                override fun onResponse(
                    call: Call<ItemAssetResponse?>, response: Response<ItemAssetResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val empItemResponse = response.body()

                        if (empItemResponse?.Status == true && empItemResponse.Data != null) {
                            val itemList = empItemResponse.Data
                            val tableLayout = binding.tableLayout
                            tableLayout.removeAllViews()

                            val headerRow = TableRow(this@AssetsItemActivity)
                            headerRow.addView(createTextView("S.No", true))
                            headerRow.addView(createTextView("Asset Name", true))
                            headerRow.addView(createTextView("Serial Number", true))
                            headerRow.addView(createTextView("TagId", true))
                            headerRow.addView(createTextView("Tag Type", true))
                            headerRow.addView(createTextView("Action", true))

                            tableLayout.addView(headerRow)

                            itemList?.forEachIndexed { index, item ->
                                val dataRow = TableRow(this@AssetsItemActivity)
                                dataRow.addView(createTextView((index + 1).toString()))
                                dataRow.addView(createTextView(item.AssetName ?: "N/A"))
                                val serialNumberView = createTextView(item.SerialNumber ?: "N/A")
                                dataRow.addView(serialNumberView)
                                val tagIdView = createTextView(item.TagId ?: "N/A")
                                val tagId = tagIdView.text.toString() // Get the tagId for the current row
                                dataRow.addView(tagIdView)
                                serialNumberView.setOnClickListener {
                                    val intent = Intent(this@AssetsItemActivity, LocatorActivity::class.java).apply {
                                        putExtra("tagId", tagId)
                                    }
                                    startActivity(intent) // Launch LocatorActivity
                                }
                                dataRow.addView(createTextView(item.TagType ?: "N/A"))

                                val isMappedText = if (item.IsMapped == true) "Unmap" else "Mapped"
                                val isMappedButton =
                                    Button(this@AssetsItemActivity) // Replace 'this' with the context if needed
                                isMappedButton.text = isMappedText
                                isMappedButton.setOnClickListener {
                                    val i = Intent(this@AssetsItemActivity, UnTaggedAssetsActivity::class.java)
                                    i.putExtra("assetName", item.AssetName)
                                    i.putExtra("serialNumber", item.SerialNumber)
                                    i.putExtra("tagType", item.TagType)
                                    i.putExtra("EMPLOYEE_CODE", employeeCode)
                                    i.putExtra("EMPLOYEE_NAME", employeeName)

                                    startActivity(i)
                                }
                                dataRow.addView(isMappedButton)
                                tableLayout.addView(dataRow)
                            }
                        } else {
                            Toast.makeText(
                                this@AssetsItemActivity, "Data Not Found", Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AssetsItemActivity, "Failed to retrieve data", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ItemAssetResponse?>, t: Throwable) {
                    Log.e("ItemAssets", "API call failed: ${t.message}")
                    Toast.makeText(
                        this@AssetsItemActivity, "Error: ${t.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /* Helper function to create TextView for table cells
     Added this code by Vikash Roy 29/10/2024*/
    private fun createTextView(text: String, isHeader: Boolean = false): TextView {
        return TextView(this).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT
            )
            this.text = text
            setBackgroundResource(R.drawable.column_border) // Set your border drawable
            setTextColor(getColor(if (isHeader) R.color.colorprimary else android.R.color.black)) // Use color for header or data
            textAlignment = View.TEXT_ALIGNMENT_CENTER // Center align text
            if (isHeader) {
                setTypeface(typeface, Typeface.BOLD) // Make header text bold
            }
            setPadding(10, 10, 10, 10) // Add padding to the TextView
        }
    }

    companion object {
        private const val TAG = "AssetsItemActivity"
    }
}

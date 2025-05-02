package com.mespl.emp_asset_mgmtapp.activities.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.SettingActivity
import com.mespl.emp_asset_mgmtapp.activities.dashboard.DashboardActivity
import com.mespl.emp_asset_mgmtapp.activities.login.DeviceStatus.DeviceStatusRequest
import com.mespl.emp_asset_mgmtapp.activities.login.DeviceStatus.DeviceStatusResponse
import com.mespl.emp_asset_mgmtapp.activities.login.LoginAPI.loginRequest
import com.mespl.emp_asset_mgmtapp.activities.login.LoginAPI.loginResponse
import com.mespl.emp_asset_mgmtapp.databinding.ActivityLoginBinding
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.utils.CacheUtils
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.utils.NetworkUtils.isNetworkAvailable
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    private lateinit var username: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: MaterialCardView
    private lateinit var deviceId: TextView

    companion object {
        const val RC_AP_PERMISSION = 123
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val perm = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        setupalllisteners()
        requestAppPermission()
        setupToolbar()
        binding.imgSetting.visibility = View.VISIBLE

        username = binding.edtusername
        passwordEt = binding.edtpassword
        loginBtn = binding.LoginBtn
        deviceId = binding.deviceId

        // Set the device ID text view
        val deviceIdValue = getDeviceIdd()
        deviceIdValue?.let {
            deviceId.text = "Device ID: $it"
        }
    }

    @SuppressLint("NewApi")
    @AfterPermissionGranted(RC_AP_PERMISSION)
    open fun requestAppPermission(): Boolean {
        var isAppPermissionAccepted = false
        if (EasyPermissions.hasPermissions(this, *perm)) {
            isAppPermissionAccepted = true
        } else {
            isAppPermissionAccepted = false
            EasyPermissions.requestPermissions(
                this, "Permissions are required", RC_AP_PERMISSION, *perm
            )
        }
        return isAppPermissionAccepted
    }

    @SuppressLint("HardwareIds")
    fun getDeviceIdd(): String? {
        return try {
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /* Created by Kalpana Yadav on 15/10/2024
      * set up all listeners  *//*Update the whole LoginBtn code for Login the user by Vikash Roy on 30/10/2024*/
    private fun setupalllisteners() {

        binding.LoginBtn.setOnClickListener {
            val username = username.text.toString()
            val password = passwordEt.text.toString()

            // Check if the username and password are empty
            if (username.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder(this)
                    .setMessage("Username and password cannot be empty") // Replace with your desired error message
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss() // Dismiss the dialog when OK is clicked
                    }.setCancelable(false).show() // Show the dialog
            } else {
                if (!isNetworkAvailable(
                        this, "Please check your internet connection and try again."
                    )
                ) {
                } else {
                    getLoginAPI(username, password)
                }
            }
            Log.e("Login Successful!", "Username: $username, Password: $password")
        }

        binding.imgSetting.setOnClickListener(View.OnClickListener {
            val i = Intent(this@LoginActivity, SettingActivity::class.java)
            startActivity(i)
        })
    }

    /* Created by Kalpana Yadav on 15/10/2024
        * UI part */
    //Update this code by Vikash Roy on 30/10/2024
    private fun initUi() {
        if (!isNetworkAvailable(this, "Please check your internet connection and try again.")) {
            DialogManager.showErrorDialog(
                this@LoginActivity, "Please check your internet connection and try again."
            )
        }
        getDeviceAPI()
    }

    /* Created by Kalpana Yadav on 15/10/2024
       * device status API for device id register */
    fun getDeviceAPI() {
        val deviceStatusRequest = DeviceStatusRequest()
        deviceStatusRequest.DeviceNumber = DialogManager.getDeviceId(this)
        println(deviceStatusRequest.DeviceNumber)

        try {
            ApiClient.client?.getDeviceStatusAPI(deviceStatusRequest)
                ?.enqueue(object : Callback<DeviceStatusResponse> {
                    override fun onResponse(
                        call: Call<DeviceStatusResponse>, response: Response<DeviceStatusResponse>
                    ) {
                        val deviceStatusResponse = response.body()
                        if (deviceStatusResponse != null) {
                            if (deviceStatusResponse.Status == false) {
                                NoInternetConnectionDialog.showErrorDialog(
                                    this@LoginActivity, deviceStatusResponse.Response.toString()
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<DeviceStatusResponse>, t: Throwable) {
                        NoInternetConnectionDialog.showErrorDialog(
                            this@LoginActivity, t.message.toString()
                        )
                    }
                })
        } catch (e: Exception) {
            Log.e("Login", "API call error: ${e.message}", e)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Login"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setTitleTextColor(
            resources.getColor(R.color.white)
        )
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).navigationIcon?.setTint(
            resources.getColor(R.color.white)
        )
        binding.toolbar.setNavigationOnClickListener {
            finishAffinity()
        }
    }

    /* Created by Kalpana Yadav on 15/10/2024
      * Login Api with credentials
      * Edit by Vikash Roy on 29/10/2024
      * Remove dialog and show toast message */
    fun getLoginAPI(usernamestr: String, passwordstr: String) {
        val loginRequest = loginRequest()
        loginRequest.EmployeeCode = usernamestr
        loginRequest.Password = passwordstr

        try {
            ApiClient.client?.callLoginAPI(loginRequest)?.enqueue(object : Callback<loginResponse> {
                override fun onResponse(
                    call: Call<loginResponse>, response: Response<loginResponse>
                ) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if (loginResponse.Status == true) {
                            val userId = loginResponse.UserId

                            // Save the userId to CacheUtils
                            userId?.let { CacheUtils.saveUserId(this@LoginActivity, it) }

                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            startActivity(intent)

                            Toast.makeText(
                                this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            AlertDialog.Builder(this@LoginActivity)
                                .setMessage("Please check your username and password")
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss() // Dismiss the dialog when OK is clicked
                                }.setCancelable(false).show()
                        }
                    }
                }

                override fun onFailure(call: Call<loginResponse>, t: Throwable) {
                    DialogManager.showErrorDialog(this@LoginActivity, t.message.toString())
                }
            })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(this@LoginActivity, e.message.toString())
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
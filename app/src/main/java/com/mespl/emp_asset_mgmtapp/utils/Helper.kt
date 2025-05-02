package com.mespl.emp_asset_mgmtapp.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object Helper {

    private var progressDialog: ProgressDialog? = null

    // Method to show ProgressDialog
    fun showProgressDialog(context: Context, message: String = "Please wait...") {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage(message)
            progressDialog?.setCancelable(false)
        }
        progressDialog?.show()
    }
    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    fun getAirlineName(airlineCode: String): String {
        var airlineName: String = ""
        if (airlineCode.equals("SG", ignoreCase = true)) {
            airlineName = "SpiceJet"
        } else if (airlineCode.equals("IC", ignoreCase = true)) {
            airlineName = "Fly91"
        } else if (airlineCode.equals("AI", ignoreCase = true)) {
            airlineName = "Air India"
        } else if (airlineCode.equals("UK", ignoreCase = true)) {
            airlineName = "Vistara"
        } else if (airlineCode.equals("6E", ignoreCase = true)) {
            airlineName = "Indigo"
        } else if (airlineCode.equals("I5", ignoreCase = true)) {
            airlineName = "AirAsia"
        } else if (airlineCode.equals("G8", ignoreCase = true)) {
            airlineName = "GoAir"
        } else if (airlineCode.equals("9I", ignoreCase = true)) {
            airlineName = "Alliance Air"
        } else if (airlineCode.equals("QP", ignoreCase = true)) {
            airlineName = "Akasa Air"
        } else {
            airlineName = "Not Available"
        }
        return airlineName
    }




    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity) // Fallback in case there is no focused view
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hidestatusbar(activity: Activity) {
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun convertTimestampToDateFormat(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set time zone to UTC

        // Define the output format (date only)
        val outputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        // Parse the timestamp into a Date object
        val date = inputFormat.parse(timestamp)
        // Format the date into the desired output format
        return outputFormat.format(date!!)
    }



}
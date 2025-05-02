package com.mespl.emp_asset_mgmtapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.login.LoginActivity


object DialogManager {
    private var dialog: Dialog? = null


    fun setStatusBarColor(color: Int, activity: Activity) {
        val window: Window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    fun showErrorDialog(context: Context, title: String) {
        if (dialog != null && dialog!!.isShowing) {
            return // Do nothing if a dialog is already being displayed
        }

        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setContentView(R.layout.passwordvalidate_layout)

        val okayBtn = dialog?.findViewById<CardView>(R.id.okay_btn)
        val errortxt = dialog?.findViewById<TextView>(R.id.errortxt)

        errortxt?.text = title

        okayBtn?.setOnClickListener {
//            closeApp(context)
            dismissDialog()
        }

        dialog?.show()
    }

    fun showSuccessDialog(context: Context, title: String, targetActivity: Class<*>) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.showsuccess_layout)

        val okay_btn: CardView = dialog.findViewById(R.id.okaybtn)
        val errortxt: TextView = dialog.findViewById(R.id.titletxt)

        errortxt.text = title

        okay_btn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(context, targetActivity)
            context.startActivity(intent)
            (context as Activity).finish()

        }
        dialog.show()
    }

    fun getDeviceId(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }

    fun closeApp(context: Context) {
        (context as Activity).finishAffinity()
    }

    fun showDialogLogout(context: Context, title: String?) {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setContentView(com.mespl.emp_asset_mgmtapp.R.layout.logoutdialog)

        val okayBtn = dialog!!.findViewById<CardView>(com.mespl.emp_asset_mgmtapp.R.id.yes_btn)
        val no_btn = dialog!!.findViewById<CardView>(com.mespl.emp_asset_mgmtapp.R.id.no_btn)
        val errortxt = dialog!!.findViewById<TextView>(com.mespl.emp_asset_mgmtapp.R.id.errortxt)

        errortxt.text = title

        okayBtn.setOnClickListener { v: View? ->
            dismissDialog()
            LogoutApp(context)
        }
        no_btn.setOnClickListener { v: View? ->
            dismissDialog()
        }

        dialog!!.show()
    }

    private fun LogoutApp(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}

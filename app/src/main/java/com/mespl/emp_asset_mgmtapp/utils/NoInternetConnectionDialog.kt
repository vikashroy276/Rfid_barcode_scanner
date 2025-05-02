package com.mespl.emp_asset_mgmtapp.utils

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mespl.emp_asset_mgmtapp.activities.login.LoginActivity

object NoInternetConnectionDialog {
    private var dialog: Dialog? = null

    fun showDialogLogout(context: Context, title: String?) {
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            window?.setBackgroundDrawableResource(R.color.transparent)
            setContentView(com.mespl.emp_asset_mgmtapp.R.layout.logoutdialog)

            findViewById<TextView>(com.mespl.emp_asset_mgmtapp.R.id.errortxt)?.text = title

            findViewById<CardView>(com.mespl.emp_asset_mgmtapp.R.id.yes_btn)?.setOnClickListener {
                dismissDialog()
                logoutApp(context)
            }

            findViewById<CardView>(com.mespl.emp_asset_mgmtapp.R.id.no_btn)?.setOnClickListener {
                dismissDialog()
            }

            show()
        }
    }

    private fun logoutApp(context: Context) {
        val intent = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun closeApp(context: Context) {
        (context as Activity).finishAffinity()
    }

    fun backApp(context: Context) {
        (context as Activity).finish()
    }

    fun dismissDialog() {
        dialog?.takeIf { it.isShowing }?.dismiss()
        dialog = null
    }

    fun showErrorDialog(context: Context, title: String) {
        if (dialog?.isShowing == true) return

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setContentView(com.mespl.emp_asset_mgmtapp.R.layout.passwordvalidate_layout)

            findViewById<TextView>(com.mespl.emp_asset_mgmtapp.R.id.errortxt)?.text = title

            findViewById<CardView>(com.mespl.emp_asset_mgmtapp.R.id.okay_btn)?.setOnClickListener {
                dismissDialog()
                closeApp(context)
            }
            show()
        }
    }
}

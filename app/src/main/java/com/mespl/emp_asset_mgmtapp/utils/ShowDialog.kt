package com.mespl.emp_asset_mgmtapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast


class ShowDialog {
    private val context: Context? = null
    private val prefs: SharedPreferences? = null
    private var dialog: Dialog? = null

    companion object {
        private val internetFailureDialog: AlertDialog? = null
        fun showToast(ctx: Context?, Message: String?) {
            Toast.makeText(ctx, Message, Toast.LENGTH_SHORT).show()
        }

        fun showToastSmall(ctx: Context?, Message: String?) {
            Toast.makeText(ctx, Message, Toast.LENGTH_SHORT).show()
        }



        fun showProgress(ctx: Context?, msg: String): ProgressDialog {
            val mProgressDialog = ProgressDialog(ctx)
            mProgressDialog.setIndeterminate(true)
            mProgressDialog.setCancelable(false)
            mProgressDialog.setTitle(msg)
            mProgressDialog.setMessage("Loading...")
            return mProgressDialog
        }

        fun dismissDialog(pDialog: ProgressDialog?) {
            if (pDialog != null && pDialog.isShowing)
                pDialog.dismiss()
        }

        /* fun showAlertDialogLogout(ctx: AppCompatActivity, userPreferences: UserPreferences) {
             val builder: AlertDialog.Builder
             builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                 AlertDialog.Builder(ctx, R.style.ThemeOverlay_Material_Dialog_Alert)
             } else {
                 AlertDialog.Builder(ctx)
             }
             builder.setCancelable(false)
             builder.setMessage(ctx.getString(R.string.logout))
             builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                 override fun onClick(dialog: DialogInterface, which: Int) {
                     userPreferences.clear()
                     ctx.finish()
                     ctx.startActivity(Intent(ctx, SplashActivity::class.java))
                     dialog.dismiss()
                 }
             })
             builder.setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
                 override fun onClick(dialog: DialogInterface, which: Int) {
                     dialog.dismiss()
                 }
             })
             builder.show()*/


    }



}


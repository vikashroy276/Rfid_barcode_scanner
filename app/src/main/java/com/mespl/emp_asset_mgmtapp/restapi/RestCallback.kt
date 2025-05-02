package com.mespl.emp_asset_mgmtapp.restapi

import android.util.Log
import com.google.gson.Gson
import com.mespl.emp_asset_mgmtapp.utils.ContainMsg
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException


abstract class RestCallback<T> : Callback<T> {
    abstract fun restResponse(isSuccess: Boolean, message: String?, response: Any?)
    override fun onFailure(call: Call<T>, t: Throwable) {
        try {
            if (t is SocketException || t is SocketTimeoutException) {
                restResponse(false, UN_SUCCESSFULL, null)
                Log.e("RestCallback", t.message!!)
            } else {
                restResponse(false, NO_CONNECTION, null)
                Log.e("RestCallback", t.message!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            try {
                Log.e("Response", Gson().toJson(response.body()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            restResponse(true, null, response.body())
        } else if (response.code() == 401) {
            val str = ContainMsg.InvalidUser
            restResponse(false, str, null)
        } else {
            restResponse(false, ContainMsg.connectioncoultnotbe, null)
        }
    }

    companion object {
        val UN_SUCCESSFULL = ContainMsg.UN_SUCCESSFULL
        val NO_CONNECTION = ContainMsg.Connectio_Error
    }
}

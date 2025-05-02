package com.mespl.emp_asset_mgmtapp.restapi

import android.app.Activity


class RetrofitRequest {
    var activity: Activity? = null
    var hasLoader = false
    var isSilent = false
    var retrofitResponse: RetrofitResponse? = null

    interface RetrofitResponse {
        fun onResponse(responseOject: Any?)
    }

    constructor()
    constructor(activity: Activity?, retrofitResponse: RetrofitResponse?) {
        this.activity = activity
        this.retrofitResponse = retrofitResponse
    }

    fun call(`object`: Any?) {
        if (hasLoader) {
            if (activity != null) {
//                ProgressDialogUtils.showLoader(activity)
            }
        }
//        val call: Call<Any> = client.addPrinter(`object`)
//        call.enqueue(object : Callback<Any?> {
//            override fun onResponse(call: Call<Any?>?, response: Response<Any?>) {
////                ProgressDialogUtils.hideLoader()
//                if (response.body() == null && !isSilent) {
////                    DialogUtils.showMessageWithOk(activity, "Something went wrong", null)
//                } else if (retrofitResponse != null) {
//                    retrofitResponse!!.onResponse(response.body())
//                }
//            }
//
//            override fun onFailure(call: Call<Any?>?, t: Throwable?) {
////                ProgressDialogUtils.hideLoader()
//                if (activity != null && !isSilent) {
////                    DialogUtils.showMessageWithOk(activity, "Something went wrong", null)
//                }
//            }
//        })
    } //    public Call<T> getCallType(Object requestObject){
    //
    //        if(requestObject instanceof AllFlightDetail){
    //            //return ApiClient.getClient().allFlightDetail((AllFlightDetail) requestObject);
    //        }
    //
    //        //return ;
    //    }
}
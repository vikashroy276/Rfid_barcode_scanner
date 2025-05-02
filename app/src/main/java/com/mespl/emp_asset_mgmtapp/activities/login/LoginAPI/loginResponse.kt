package com.mespl.emp_asset_mgmtapp.activities.login.LoginAPI

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class loginResponse {

    @SerializedName("UserId")
    @Expose
    var UserId: String? = null

    @SerializedName("Status")
    @Expose
    var Status: Boolean? = false

    @SerializedName("Response")
    @Expose
    var Response: String? = null
}
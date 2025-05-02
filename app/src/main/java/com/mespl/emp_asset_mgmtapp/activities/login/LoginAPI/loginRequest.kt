package com.mespl.emp_asset_mgmtapp.activities.login.LoginAPI

import com.google.gson.annotations.SerializedName

class loginRequest {
    @SerializedName("EmployeeCode")
    var EmployeeCode : String? = null

    @SerializedName("Password")
    var Password : String? = null
}
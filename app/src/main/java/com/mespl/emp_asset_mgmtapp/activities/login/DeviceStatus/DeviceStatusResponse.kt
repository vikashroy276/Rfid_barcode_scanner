package com.mespl.emp_asset_mgmtapp.activities.login.DeviceStatus

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeviceStatusResponse {

    @SerializedName("Status")
    @Expose
    var Status: Boolean? = false

    @SerializedName("Response")
    @Expose
    var Response: String? = null
}
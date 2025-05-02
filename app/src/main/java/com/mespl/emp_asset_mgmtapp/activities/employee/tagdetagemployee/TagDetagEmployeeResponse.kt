package com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TagDetagEmployeeResponse {
    @SerializedName("Status")
    @Expose
    var status: Boolean? = null

    @SerializedName("Response")
    @Expose
    var response: String? = null
}
package com.mespl.emp_asset_mgmtapp.activities.dashboard.employeecount

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EmployeeCountResponse {

    @SerializedName("Status")
    @Expose
    var Status: Boolean? = false

    @SerializedName("Response")
    @Expose
    var Response: String? = null

    @SerializedName("TotalCount")
    @Expose
    var TotalCount: Int? = null
}
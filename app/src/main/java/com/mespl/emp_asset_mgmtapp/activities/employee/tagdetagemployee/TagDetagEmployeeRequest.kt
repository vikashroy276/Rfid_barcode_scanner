package com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee

import com.google.gson.annotations.SerializedName

class TagDetagEmployeeRequest {
    @SerializedName("Action")
    var action: String? = null

    @SerializedName("EmployeeCode")
    var EmployeeCode: String? = null

    @SerializedName("IsTagged")
    var IsTagged: Boolean? = false

    @SerializedName("TagType")
    var TagType: String? = null

    @SerializedName("TagId")
    var TagId: String? = null

    @SerializedName("TagValue")
    var TagValue: String? = null

    @SerializedName("TaggedBy")
    var TaggedBy: String? = null



}
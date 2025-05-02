package com.mespl.emp_asset_mgmtapp.activities.employee.nontaggedtmployeelist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NonTaggedEmployeeListResponse {

    @SerializedName("Status")
    @Expose
    var status: Boolean? = null

    @SerializedName("Response")
    @Expose
    var response: String? = null

    @SerializedName("NonTaggedEmployeeList")
    @Expose
    var nonTaggedEmployeeList: List<NonTaggedEmployee>? = null

    class NonTaggedEmployee : Serializable {

        @SerializedName("fName")
        @Expose
        var fName: String? = null

        @SerializedName("lName")
        @Expose
        var lName: String? = null

        @SerializedName("EmailId")
        @Expose
        var emailId: String? = null

        @SerializedName("ContactNo")
        @Expose
        var contactNo: String? = null

        @SerializedName("CreatedAt")
        @Expose
        var createdAt: String? = null

        @SerializedName("IsActive")
        @Expose
        var isActive: Boolean? = null

        @SerializedName("EmployeeCode")
        @Expose
        var employeeCode: String? = null

        @SerializedName("Role")
        @Expose
        var role: String? = null

        @SerializedName("Designation")
        @Expose
        var designation: String? = null

        @SerializedName("TagType")
        @Expose
        var tagType: String? = null
    }
}

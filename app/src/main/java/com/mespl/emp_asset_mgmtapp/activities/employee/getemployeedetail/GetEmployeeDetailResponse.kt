package com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetEmployeeDetailResponse {

    @SerializedName("Status")
    @Expose
    var status: Boolean? = null

    @SerializedName("Response")
    @Expose
    var response: String? = null

    @SerializedName("Details")
    @Expose
    var details: Details? = null  // Change back to a single object

    class Details {

        @SerializedName("fName")
        @Expose
        var fName: String? = null

        @SerializedName("lName")
        @Expose
        var lName: String? = null

        @SerializedName("IsActive")
        @Expose
        var isActive: Boolean? = null

        @SerializedName("IsDeleted")
        @Expose
        var isDeleted: Boolean? = null

        @SerializedName("EmployeeCode")
        @Expose
        var employeeCode: String? = null

        @SerializedName("Designation")
        @Expose
        var designation: String? = null

        @SerializedName("IsTagged")
        @Expose
        var isTagged: Boolean? = null

        @SerializedName("TagType")
        @Expose
        var tagType: String? = null

        @SerializedName("Department")
        @Expose
        var department: String? = null

        @SerializedName("ContactNo")
        @Expose
        var contactNo: String? = null
    }
}



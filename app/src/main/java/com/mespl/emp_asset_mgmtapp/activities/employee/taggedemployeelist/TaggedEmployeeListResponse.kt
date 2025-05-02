package com.mespl.emp_asset_mgmtapp.activities.employee.taggedemployeelist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

 class TaggedEmployeeListResponse {

    @SerializedName("Status")
    @Expose
    var status: Boolean? = null

    @SerializedName("Response")
    @Expose
    var response: String? = null

    @SerializedName("TaggedEmployeeList")
    @Expose
    var taggedEmployeeList: List<TaggedEmployee>? = null

    // Employee class implementing Serializable for passing objects through intents
    class TaggedEmployee : Serializable {

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

        @SerializedName("AssetCount")
        @Expose
        var assetCount: String? = null
    }
}


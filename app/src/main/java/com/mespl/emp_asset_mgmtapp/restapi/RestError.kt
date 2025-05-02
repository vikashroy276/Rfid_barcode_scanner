package com.mespl.emp_asset_mgmtapp.restapi

import com.google.gson.annotations.SerializedName


class RestError    //Getters and setters
    (@field:SerializedName("ErrorMessage") private val strMessage: String) {
    @SerializedName("code")
    private val code: Int? = null
}
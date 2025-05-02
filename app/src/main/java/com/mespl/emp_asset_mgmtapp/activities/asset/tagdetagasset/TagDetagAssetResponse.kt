package com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TagDetagAssetResponse {
    @SerializedName("Status")
    @Expose
    var status: Boolean? = null

    @SerializedName("Response")
    @Expose
    var response: String? = null
}
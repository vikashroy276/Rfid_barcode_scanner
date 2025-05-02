package com.mespl.emp_asset_mgmtapp.activities.dashboard.assetcount

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssetCountResponse {

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
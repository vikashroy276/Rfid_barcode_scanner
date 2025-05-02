package com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapUnmapAssetResponse {
    @SerializedName("Status")
    @Expose
    var status: Boolean? = null

    @SerializedName("Response")
    @Expose
    var Response: String? = null
}
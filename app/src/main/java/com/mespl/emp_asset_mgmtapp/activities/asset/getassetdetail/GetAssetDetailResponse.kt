package com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetAssetDetailResponse {
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

        @SerializedName("SerialNumber")
        @Expose
        var serialNumber: String? = null

        @SerializedName("AssetName")
        @Expose
        var assetName: String? = null

        @SerializedName("IsActive")
        @Expose
        var isActive: Boolean? = null

        @SerializedName("Category")
        @Expose
        var Category: String? = null

        @SerializedName("IsTagged")
        @Expose
        var isTagged: Boolean? = null

        @SerializedName("TagType")
        @Expose
        var TagType: String? = null


    }
}

package com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset

import com.google.gson.annotations.SerializedName

class TagDetagAssetRequest {
    @SerializedName("Action")
    var action: String? = null

    @SerializedName("SerialNumber")
    var SerialNumber: String? = null

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
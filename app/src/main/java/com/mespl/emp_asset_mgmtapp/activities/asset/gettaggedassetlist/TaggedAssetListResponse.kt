package com.mespl.emp_asset_mgmtapp.activities.asset.gettaggedassetlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TaggedAssetListResponse {
     @SerializedName("Status")
     @Expose
     var status: Boolean? = null

     @SerializedName("Response")
     @Expose
     var response: String? = null

     @SerializedName("TaggedAssetList")
     @Expose
     var taggedAssetList: List<TaggedAsset>? = null

     class TaggedAsset:Serializable{

         @SerializedName("Category")
         @Expose
         var Category: String? = null

         @SerializedName("SerialNumber")
         @Expose
         var SerialNumber: String? = null


         @SerializedName("AssetName")
         @Expose
         var AssetName: String? = null

         @SerializedName("IsActive")
         @Expose
         var IsActive: Boolean? = null

         @SerializedName("IsTagged")
         @Expose
         var IsTagged: Boolean? = null

         @SerializedName("TagType")
         @Expose
         var TagType: String? = null

         @SerializedName("IsMapped")
         @Expose
         var IsMapped: Boolean? = null
     }
 }

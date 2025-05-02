package com.mespl.emp_asset_mgmtapp.activities.asset.ItemAsset

import java.io.Serializable

class ItemAssetResponse{

    var Status : Boolean? = null
    var message : String? = null
    var Count: Int = 0
    var Data : List<ItemAssetData>? = null

    class ItemAssetData : Serializable {
        var SerialNumber : String? = null
        var AssetName : String? = null
        var TagType : String? = null
        var TagId : String? = null
        var IsMapped: Boolean? = null
    }
}



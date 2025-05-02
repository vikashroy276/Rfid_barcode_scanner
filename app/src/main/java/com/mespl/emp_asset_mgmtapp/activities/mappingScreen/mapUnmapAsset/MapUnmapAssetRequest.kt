package com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset

import java.io.Serializable

class MapUnmapAssetRequest {
    var Action: String? = null
    var EmployeeId: String? = null
    var MappedBy: String? = null
    var Remarks: String? = null
    var Assets: List<Asset>? = null
}

class Asset : Serializable {
    var Id: String? = null
}

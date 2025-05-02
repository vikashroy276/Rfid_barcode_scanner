package com.mespl.emp_asset_mgmtapp.restapi

import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.GetAssetDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.getnontaggedassetlist.NonTaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.gettaggedassetlist.TaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.ItemAsset.ItemAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.ItemAsset.ItemAssetResponse
import com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset.TagDetagAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.asset.tagdetagasset.TagDetagAssetResponse
import com.mespl.emp_asset_mgmtapp.activities.dashboard.assetcount.AssetCountResponse
import com.mespl.emp_asset_mgmtapp.activities.dashboard.employeecount.EmployeeCountResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.getemployeedetail.GetEmployeeDetailResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.nontaggedtmployeelist.NonTaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee.TagDetagEmployeeRequest
import com.mespl.emp_asset_mgmtapp.activities.employee.tagdetagemployee.TagDetagEmployeeResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.taggedemployeelist.TaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.activities.login.DeviceStatus.DeviceStatusRequest
import com.mespl.emp_asset_mgmtapp.activities.login.DeviceStatus.DeviceStatusResponse
import com.mespl.emp_asset_mgmtapp.activities.login.LoginAPI.loginRequest
import com.mespl.emp_asset_mgmtapp.activities.login.LoginAPI.loginResponse
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.MapUnmapAssetRequest
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.mapUnmapAsset.MapUnmapAssetResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("deviceStatus")
    fun getDeviceStatusAPI(@Body deviceStatusRequest: DeviceStatusRequest): Call<DeviceStatusResponse>

    @POST("login")
    fun callLoginAPI(@Body loginResponse: loginRequest): Call<loginResponse>

    @GET("employeeCount")
    fun callemployeeCountAPI(): Call<EmployeeCountResponse>

    @GET("assetCount")
    fun callassetCountAPI(): Call<AssetCountResponse>

    @POST("getEmployeeDetail")
    fun callEmployeedetailAPI(@Body employeedetailrequest: GetEmployeeDetailRequest): Call<GetEmployeeDetailResponse>

    @POST("getAssetDetail")
    fun callAssetDetailAPI(@Body loginResponse: GetAssetDetailRequest): Call<GetAssetDetailResponse>

    @GET("getTaggedEmployeeList")
    fun callTaggedEmployeeListAPI(): Call<TaggedEmployeeListResponse>

    @GET("getNonTaggedEmployeeList")
    fun callNonTaggedEmployeeListAPI(): Call<NonTaggedEmployeeListResponse>

    @GET("getTaggedAssetList")
    fun callTaggedAssetListAPI(): Call<TaggedAssetListResponse>

    @GET("getNonTaggedAssetList")
    fun callNonTaggedAssetListAPI(): Call<NonTaggedAssetListResponse>

    @POST("tagDetagEmployee")
    fun calltagDetagEmployeeAPI(@Body tagDetagEmployeerequest: TagDetagEmployeeRequest): Call<TagDetagEmployeeResponse>

    @POST("tagDetagAsset")
    fun calltagDetagAssetAPI(@Body tagDetagAsseterequest: TagDetagAssetRequest): Call<TagDetagAssetResponse>

    @POST("mapUnmapAsset")
    fun callmapUnmapAssetAPI(@Body mapunmapassetrequest: MapUnmapAssetRequest): Call<MapUnmapAssetResponse>

    @POST("getMappedAssetsByEmpCode")
    fun callgetMappedAssetsByEmpCodeAPI(@Body itemassestsrequest: ItemAssetRequest): Call<ItemAssetResponse>

}

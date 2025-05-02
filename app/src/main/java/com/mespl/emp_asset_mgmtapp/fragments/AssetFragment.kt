package com.mespl.emp_asset_mgmtapp.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.activities.asset.AssetActivity
import com.mespl.emp_asset_mgmtapp.activities.asset.gettaggedassetlist.TaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.adapters.AssetTaggedListAdapter
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class AssetFragment : Fragment(), AssetTaggedListAdapter.OnAssetClickListener {

    private lateinit var asset_recycler: RecyclerView
    private lateinit var assettaggedAdapter: AssetTaggedListAdapter
    private val taggedAssetList: MutableList<TaggedAssetListResponse.TaggedAsset> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_asset, container, false)
        asset_recycler = view.findViewById(R.id.asset_recycler)
        asset_recycler.layoutManager = LinearLayoutManager(context)
        assettaggedAdapter = AssetTaggedListAdapter(taggedAssetList,this)
        asset_recycler.adapter = assettaggedAdapter
        callTaggedAssetListAPI()

        return view
    }

    private fun callTaggedAssetListAPI() {
        try {
            ApiClient.client?.callTaggedAssetListAPI()?.enqueue(object :
                Callback<TaggedAssetListResponse> {
                override fun onResponse(
                    call: Call<TaggedAssetListResponse>,
                    response: Response<TaggedAssetListResponse>
                ) {
                    val assettaggedListResponse = response.body()
                    if (assettaggedListResponse?.status == true) {
                        // Update taggedEmployeeList with the response data
                        taggedAssetList.clear()
                        assettaggedListResponse.taggedAssetList?.let {
                            taggedAssetList.addAll(it)
                        }
                        // Notify the adapter of data changes
                        assettaggedAdapter.notifyDataSetChanged()
                    } else {
                        // Handle case when there's no data
                        DialogManager.showErrorDialog(requireActivity(), "No Assets found.")
                    }
                }

                override fun onFailure(call: Call<TaggedAssetListResponse>, t: Throwable) {
                    // Show error dialog in case of failure
                    DialogManager.showErrorDialog(requireActivity(), t.message.toString())
                }
            })
        } catch (e: Exception) {
            // Show error dialog in case of an exception
            DialogManager.showErrorDialog(requireActivity(), e.message.toString())
        }
    }

    override fun onAssetClick(assets: TaggedAssetListResponse.TaggedAsset) {
        val intent = Intent(requireActivity(), AssetActivity::class.java).apply {
            putExtra("assetlist", assets as Serializable)
        }
        startActivity(intent)
    }
}




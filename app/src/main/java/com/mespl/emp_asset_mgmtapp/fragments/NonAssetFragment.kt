package com.mespl.emp_asset_mgmtapp.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.asset.NonTaggedAssetActivity
import com.mespl.emp_asset_mgmtapp.activities.asset.getnontaggedassetlist.NonTaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.adapters.NonAssetTaggedListAdapter
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class NonAssetFragment : Fragment(), NonAssetTaggedListAdapter.OnNonAssetClickListener {

    private lateinit var nonasset_recycler: RecyclerView
    private lateinit var nonassettaggedAdapter: NonAssetTaggedListAdapter
    private val nontaggedAssetList: MutableList<NonTaggedAssetListResponse.NonTaggedAsset> =
        mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nonasset, container, false)
        nonasset_recycler = view.findViewById(R.id.nonasset_recycler)
        nonasset_recycler.layoutManager = LinearLayoutManager(context)
        nonassettaggedAdapter = NonAssetTaggedListAdapter(nontaggedAssetList, this)
        nonasset_recycler.adapter = nonassettaggedAdapter
        callNonTaggedAssetListAPI()

        return view
    }

    private fun callNonTaggedAssetListAPI() {
        try {
            ApiClient.client?.callNonTaggedAssetListAPI()
                ?.enqueue(object : Callback<NonTaggedAssetListResponse> {
                    override fun onResponse(
                        call: Call<NonTaggedAssetListResponse>,
                        response: Response<NonTaggedAssetListResponse>
                    ) {
                        val nonassettaggedListResponse = response.body()
                        if (nonassettaggedListResponse?.status == true) {
                            nontaggedAssetList.clear()
                            nonassettaggedListResponse.nonTaggedAssetList?.let {
                                nontaggedAssetList.addAll(it)
                            }
                            nonassettaggedAdapter.notifyDataSetChanged()
                        } else {
                            DialogManager.showErrorDialog(requireActivity(), "No employees found.")
                        }
                    }

                    override fun onFailure(call: Call<NonTaggedAssetListResponse>, t: Throwable) {
                        DialogManager.showErrorDialog(requireActivity(), t.message.toString())
                    }
                })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(requireActivity(), e.message.toString())
        }
    }

    override fun onNonAssetClick(nonassets: NonTaggedAssetListResponse.NonTaggedAsset) {
        val intent = Intent(requireActivity(), NonTaggedAssetActivity::class.java).apply {
            putExtra("nonassetlist", nonassets as Serializable)
        }
        startActivity(intent)
    }
}




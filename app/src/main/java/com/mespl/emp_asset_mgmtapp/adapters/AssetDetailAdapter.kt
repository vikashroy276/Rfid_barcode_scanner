package com.mespl.emp_asset_mgmtapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.activities.asset.getassetdetail.AssetsDetailsResponse
import com.mespl.emp_asset_mgmtapp.R

class AssetDetailAdapter : RecyclerView.Adapter<AssetDetailAdapter.AssetDetailViewHolder>() {

    private val assetDetails = mutableListOf<AssetsDetailsResponse>()

    fun addAssetDetail(assetDetail: AssetsDetailsResponse) {
        assetDetails.add(assetDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetDetailViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.assetsitem, parent, false)
        return AssetDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssetDetailViewHolder, position: Int) {
        holder.bind(assetDetails[position])
    }

    override fun getItemCount(): Int = assetDetails.size

    inner class AssetDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvsrlNo: TextView = itemView.findViewById(R.id.tvsrlNo)
        private val assetNameTextView: TextView = itemView.findViewById(R.id.tvassetsName)

        fun bind(assetDetail: AssetsDetailsResponse) {
            tvsrlNo.text = assetDetail.serialNumber
            assetNameTextView.text = assetDetail.assetName
        }
    }
}


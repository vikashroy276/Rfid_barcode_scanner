package com.mespl.emp_asset_mgmtapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.activities.asset.gettaggedassetlist.TaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.databinding.EmployeelayoutBinding

class AssetTaggedListAdapter(private val assettaggedList: List<TaggedAssetListResponse.TaggedAsset>,
                             private val listener: OnAssetClickListener) :
    RecyclerView.Adapter<AssetTaggedListAdapter.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = EmployeelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val assetdata = assettaggedList[position]
        holder.bind(assetdata)

        holder.itemView.setOnClickListener {
            listener.onAssetClick(assetdata)
        }
    }
    interface OnAssetClickListener {
        fun onAssetClick(employee: TaggedAssetListResponse.TaggedAsset)
    }

    override fun getItemCount(): Int = assettaggedList.size

    class EmployeeViewHolder(private val binding: EmployeelayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assetdata: TaggedAssetListResponse.TaggedAsset) {
            binding.empName.text = assetdata.AssetName
            binding.empDesignation.text = assetdata.Category
            binding.empDepartment.text = assetdata.IsMapped.toString()
            val cardBackgroundColor = if (assetdata.IsMapped == true) {
                Color.LTGRAY
            } else {
                Color.WHITE
            }
            binding.cardView.setCardBackgroundColor(cardBackgroundColor)
            binding.empItem.visibility = View.GONE
        }
    }
}

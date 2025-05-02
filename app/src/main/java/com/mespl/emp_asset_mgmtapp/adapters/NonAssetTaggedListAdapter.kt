package com.mespl.emp_asset_mgmtapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.activities.asset.getnontaggedassetlist.NonTaggedAssetListResponse
import com.mespl.emp_asset_mgmtapp.databinding.EmployeelayoutBinding

class NonAssetTaggedListAdapter(private val nonassettaggedList: List<NonTaggedAssetListResponse.NonTaggedAsset>,
                                private val listener: OnNonAssetClickListener
) :
    RecyclerView.Adapter<NonAssetTaggedListAdapter.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = EmployeelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val nonassetdata = nonassettaggedList[position]
        holder.bind(nonassetdata)

        // Set a click listener on the root view
        holder.itemView.setOnClickListener {
            listener.onNonAssetClick(nonassetdata)
        }
    }
    interface OnNonAssetClickListener {
        fun onNonAssetClick(nonasset: NonTaggedAssetListResponse.NonTaggedAsset)
    }

    override fun getItemCount(): Int = nonassettaggedList.size

    class EmployeeViewHolder(private val binding: EmployeelayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nonassetdata: NonTaggedAssetListResponse.NonTaggedAsset) {
            binding.empName.text = nonassetdata.AssetName
            binding.empDesignation.text = nonassetdata.Category
            binding.empDepartment.text = nonassetdata.IsActive.toString()
            binding.empItem.visibility = View.GONE

        }
    }
}

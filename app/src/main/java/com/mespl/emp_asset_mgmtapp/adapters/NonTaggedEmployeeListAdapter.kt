package com.mespl.emp_asset_mgmtapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.activities.employee.nontaggedtmployeelist.NonTaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.databinding.NonemployeelayoutBinding

class NonTaggedEmployeeListAdapter(private val nontaggedemployeeList: List<NonTaggedEmployeeListResponse.NonTaggedEmployee>,
                                   private val listener: OnNonTaggedEmployeeClickListener
) :
    RecyclerView.Adapter<NonTaggedEmployeeListAdapter.NonTaggedEmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NonTaggedEmployeeViewHolder {
        val binding = NonemployeelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NonTaggedEmployeeViewHolder(binding)
    }
    interface OnNonTaggedEmployeeClickListener {
        fun onNonTaggedEmployeeClick(employee: NonTaggedEmployeeListResponse.NonTaggedEmployee)
    }

    override fun onBindViewHolder(holder: NonTaggedEmployeeViewHolder, position: Int) {
        val employee = nontaggedemployeeList[position]
        holder.bind(employee)
        holder.itemView.setOnClickListener {
            listener.onNonTaggedEmployeeClick(employee)
        }
    }

    override fun getItemCount(): Int = nontaggedemployeeList.size

    class NonTaggedEmployeeViewHolder(private val binding: NonemployeelayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: NonTaggedEmployeeListResponse.NonTaggedEmployee) {
            binding.empName.text = "${employee.fName} ${employee.lName}"
            binding.empmaildid.text = employee.emailId
            binding.empDesignation.text = employee.designation ?: "Not Assigned" // Handling null case
            binding.empDepartment.text = employee.role

        }
    }
}

package com.mespl.emp_asset_mgmtapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.activities.employee.AssetsItemActivity
import com.mespl.emp_asset_mgmtapp.activities.employee.taggedemployeelist.TaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.databinding.EmployeelayoutBinding

class TaggedEmployeeListAdapter(
    private val employeeList: List<TaggedEmployeeListResponse.TaggedEmployee>,
    private val listener: OnEmployeeClickListener
) :
    RecyclerView.Adapter<TaggedEmployeeListAdapter.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding =
            EmployeelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bind(employee)

        holder.itemView.setOnClickListener {
            listener.onEmployeeClick(employee)
        }

    }

    interface OnEmployeeClickListener {
        fun onEmployeeClick(employee: TaggedEmployeeListResponse.TaggedEmployee)
    }

    override fun getItemCount(): Int = employeeList.size

    class EmployeeViewHolder(private val binding: EmployeelayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: TaggedEmployeeListResponse.TaggedEmployee) {
            binding.empName.text = "${employee.fName} ${employee.lName}"
            binding.empmaildid.text = employee.emailId
            binding.empDesignation.text =
                employee.designation ?: "Not Assigned" // Handling null case
            binding.empDepartment.text = employee.role
            binding.empStatus.text = employee.tagType
            binding.empItem.text = employee.assetCount

            // Disable empItem button if assetCount is 0
            val assetCount =
                employee.assetCount?.toIntOrNull() ?: 0 // Safely convert assetCount to an integer
            binding.empItem.isEnabled =
                assetCount > 0 // Enable if assetCount > 0, disable otherwise

            binding.empItem.setOnClickListener {
                if (assetCount > 0) { // Only proceed if assetCount is greater than 0
                    val intent =
                        Intent(binding.root.context, AssetsItemActivity::class.java).apply {
                            putExtra("EMPLOYEE_NAME", "${employee.fName} ${employee.lName}")
                            putExtra("EMPLOYEE_CODE", employee.employeeCode)
                        }
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }
}

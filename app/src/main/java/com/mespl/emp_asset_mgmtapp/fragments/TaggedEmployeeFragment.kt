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
import com.mespl.emp_asset_mgmtapp.activities.asset.ItemAsset.ItemAssetResponse
import com.mespl.emp_asset_mgmtapp.activities.employee.EmployeeActivity
import com.mespl.emp_asset_mgmtapp.activities.employee.taggedemployeelist.TaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.adapters.TaggedEmployeeListAdapter
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class TaggedEmployeeFragment : Fragment(), TaggedEmployeeListAdapter.OnEmployeeClickListener {

    private lateinit var taggedemp_recycler: RecyclerView
    private lateinit var taggedemployeeAdapter: TaggedEmployeeListAdapter
    private val taggedEmployeeList: MutableList<TaggedEmployeeListResponse.TaggedEmployee> =
        mutableListOf()
    private val empItemList: MutableList<ItemAssetResponse.ItemAssetData> = mutableListOf()
    private var employeeCode: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tagged, container, false)

        taggedemp_recycler = view.findViewById(R.id.taggedmp_recycler)
        taggedemp_recycler.layoutManager = LinearLayoutManager(context)
        taggedemployeeAdapter = TaggedEmployeeListAdapter(taggedEmployeeList, this)
        taggedemp_recycler.adapter = taggedemployeeAdapter
        callTaggedEmployeeListAPI()
        return view
    }

    /* Update This Code By Vikash
    * Date 27/10/2024 */
    private fun callTaggedEmployeeListAPI() {
        try {
            ApiClient.client?.callTaggedEmployeeListAPI()
                ?.enqueue(object : Callback<TaggedEmployeeListResponse> {
                    override fun onResponse(
                        call: Call<TaggedEmployeeListResponse>,
                        response: Response<TaggedEmployeeListResponse>
                    ) {
                        val employeeListResponse = response.body()
                        if (employeeListResponse?.status == true) {
                            taggedEmployeeList.clear()
                            employeeListResponse.taggedEmployeeList?.let { taggedEmployees ->
                                taggedEmployeeList.addAll(taggedEmployees)

                                employeeCode =
                                    listOf(taggedEmployees.map { it.employeeCode ?: "" }.toString())
                            }
                            taggedemployeeAdapter.notifyDataSetChanged()
                        } else {
                            DialogManager.showErrorDialog(requireActivity(), "No employees found.")
                        }
                    }
                    override fun onFailure(call: Call<TaggedEmployeeListResponse>, t: Throwable) {
                        DialogManager.showErrorDialog(requireActivity(), t.message.toString())
                    }
                })
        } catch (e: Exception) {
            // Show error dialog in case of an exception
            DialogManager.showErrorDialog(requireActivity(), e.message.toString())
        }
    }


    override fun onEmployeeClick(employee: TaggedEmployeeListResponse.TaggedEmployee) {
        val intent = Intent(requireActivity(), EmployeeActivity::class.java).apply {
            putExtra("employee", employee as Serializable)
            putExtra("Count", empItemList as Serializable)
        }
        startActivity(intent)
    }
}






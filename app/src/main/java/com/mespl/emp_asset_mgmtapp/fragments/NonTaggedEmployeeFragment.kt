package com.mespl.emp_asset_mgmtapp.fragments


import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mespl.emp_asset_mgmtapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.mespl.emp_asset_mgmtapp.activities.employee.NonTaggedEmployeeActivity
import com.mespl.emp_asset_mgmtapp.activities.employee.nontaggedtmployeelist.NonTaggedEmployeeListResponse
import com.mespl.emp_asset_mgmtapp.adapters.NonTaggedEmployeeListAdapter
import com.mespl.emp_asset_mgmtapp.utils.DialogManager
import com.mespl.emp_asset_mgmtapp.restapi.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class NonTaggedEmployeeFragment : Fragment() , NonTaggedEmployeeListAdapter.OnNonTaggedEmployeeClickListener {

    private lateinit var nontagged_recycler: RecyclerView
    private lateinit var nontaggedemployeeAdapter: NonTaggedEmployeeListAdapter
    private val nontaggedEmployeeList: MutableList<NonTaggedEmployeeListResponse.NonTaggedEmployee> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_non_tagged, container, false)

        // Initialize RecyclerView
        nontagged_recycler = view.findViewById(R.id.nontagged_recycler)
        nontagged_recycler.layoutManager = LinearLayoutManager(context)

        // Initialize adapter with an empty list
        nontaggedemployeeAdapter = NonTaggedEmployeeListAdapter(nontaggedEmployeeList,this)
        nontagged_recycler.adapter = nontaggedemployeeAdapter

        // Call the API to load tagged employees when the fragment loads
        callNonTaggedEmployeeListAPI()

        return view
    }

    private fun callNonTaggedEmployeeListAPI() {
        try {
            ApiClient.client?.callNonTaggedEmployeeListAPI()?.enqueue(object :
                Callback<NonTaggedEmployeeListResponse> {
                override fun onResponse(
                    call: Call<NonTaggedEmployeeListResponse>,
                    response: Response<NonTaggedEmployeeListResponse>
                ) {
                    val nonemployeeListResponse = response.body()
                    if (nonemployeeListResponse?.status == true) {
                        nontaggedEmployeeList.clear()
                        nonemployeeListResponse.nonTaggedEmployeeList?.let {
                            nontaggedEmployeeList.addAll(it)
                        }
                        nontaggedemployeeAdapter.notifyDataSetChanged()
                    } else {
                        DialogManager.showErrorDialog(requireActivity(), "Non-Tagged employees not found.")
                    }
                }

                override fun onFailure(call: Call<NonTaggedEmployeeListResponse>, t: Throwable) {
                    DialogManager.showErrorDialog(requireActivity(), t.message.toString())
                }
            })
        } catch (e: Exception) {
            DialogManager.showErrorDialog(requireActivity(), e.message.toString())
        }
    }
    override fun onNonTaggedEmployeeClick(employee: NonTaggedEmployeeListResponse.NonTaggedEmployee) {
        val intent = Intent(requireActivity(), NonTaggedEmployeeActivity::class.java).apply {
            putExtra("employee", employee as Serializable)
        }
        startActivity(intent)
    }




}




package com.mespl.emp_asset_mgmtapp.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mespl.emp_asset_mgmtapp.R
import com.mespl.emp_asset_mgmtapp.activities.dashboard.DashboardActivity
import com.mespl.emp_asset_mgmtapp.adapters.ViewPagerAdapter
import com.mespl.emp_asset_mgmtapp.databinding.ActivityEmployeeListBinding
import com.mespl.emp_asset_mgmtapp.fragments.NonTaggedEmployeeFragment
import com.mespl.emp_asset_mgmtapp.fragments.TaggedEmployeeFragment
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog


class EmployeeListActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    lateinit var binding: ActivityEmployeeListBinding
    private var isProcessingClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        setupViewPager()

        /*This is Logout Button code for logout from the app
        * this is ImageView Button code for logout from the app  */
        binding.imgLogout.setOnClickListener {
            if (!isProcessingClick) {
                isProcessingClick = true
                binding.imgLogout.isEnabled = false
                NoInternetConnectionDialog.showDialogLogout(
                    this, "Are you sure you want to logout?"
                )
                startLogoutCooldown()
            }
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(TaggedEmployeeFragment(), "Tagged")
        adapter.addFragment(NonTaggedEmployeeFragment(), "Non-Tagged")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    /*This Function is creating for logout button code and give one second delay time on Logout Button */
    private fun startLogoutCooldown() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isProcessingClick = false
                binding.imgLogout.isEnabled = true
            }
        }.start()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Employee List"
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .setTitleTextColor(resources.getColor(R.color.white))

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .navigationIcon?.setTint(resources.getColor(R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}



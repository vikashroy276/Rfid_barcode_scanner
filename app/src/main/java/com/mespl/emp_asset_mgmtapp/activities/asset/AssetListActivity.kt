package com.mespl.emp_asset_mgmtapp.activities.asset

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
import com.mespl.emp_asset_mgmtapp.databinding.ActivityAssetListBinding
import com.mespl.emp_asset_mgmtapp.fragments.AssetFragment
import com.mespl.emp_asset_mgmtapp.fragments.NonAssetFragment
import com.mespl.emp_asset_mgmtapp.utils.NoInternetConnectionDialog

class AssetListActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    lateinit var binding: ActivityAssetListBinding
    private var isProcessingClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        setupViewPager()

        /*This is Logout Button code for logout from the app
        * this is ImageView Button code for logout from the app
        * Added this code by Vikash Roy 25/10/2024 */
        binding.imgLogout.setOnClickListener {
            Log.e("Print", "Clicked ")
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

    /*This Function is creating for logout button code and give one second delay time on Logout Button
    * Added this code by Vikash Roy 25/10/2024 */
    private fun startLogoutCooldown() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isProcessingClick = false
                binding.imgLogout.isEnabled = true
            }
        }.start()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(AssetFragment(), "Tagged")
        adapter.addFragment(NonAssetFragment(), "Non-Tagged")
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Asset List"
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
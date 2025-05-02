package com.mespl.emp_asset_mgmtapp.utils

import android.app.Application

class App : Application() {

    companion object {
        var app: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}

package com.ishaan.quickcart

import android.app.Application

class QuickCartApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
    }
}
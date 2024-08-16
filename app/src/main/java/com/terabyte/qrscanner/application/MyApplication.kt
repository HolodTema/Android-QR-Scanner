package com.terabyte.qrscanner.application

import android.app.Application
import com.terabyte.qrscanner.BuildConfig
import timber.log.Timber

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.IS_DEBUGGABLE) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
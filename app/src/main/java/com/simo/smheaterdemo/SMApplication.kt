package com.simo.smheaterdemo

import android.app.Application
import com.simo.smheatersdk.SMBLEManager
import timber.log.Timber

/**
 * Created by GrayLand119
 * on 2020/6/9
 */
class SMApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Timber initialized")
        SMBLEManager.instance.attachContext(this)
    }
}
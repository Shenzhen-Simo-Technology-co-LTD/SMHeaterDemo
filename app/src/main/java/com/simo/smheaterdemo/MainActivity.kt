package com.simo.smheaterdemo

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.simo.smheatersdk.BLELog
import com.simo.smheatersdk.SMBLEManager

const val REQUEST_ENABLE_BT = 1
const val REQUEST_LOCATION = 2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
    }

    fun requestPermissions() {
        BLELog.d("setupPermissions")
        val permissionReq = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            BLELog.d("Permission to BLUETOOTH denied")
            permissionReq.add(Manifest.permission.BLUETOOTH)
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            BLELog.d("Permission to BLUETOOTH_ADMIN denied")
            permissionReq.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            BLELog.d("Permission to ACCESS_FINE_LOCATION denied")
            permissionReq.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionReq.size > 0) {
//            ActivityCompat.requestPermissions(this, permissionReq.toTypedArray(), REQUEST_LOCATION)
            requestPermissions(permissionReq.toTypedArray(), REQUEST_LOCATION)
        }
    }
}


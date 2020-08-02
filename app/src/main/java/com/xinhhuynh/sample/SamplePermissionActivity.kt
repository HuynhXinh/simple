package com.xinhhuynh.sample

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xinhhuynh.simplepermission.PermissionManager
import com.xinhhuynh.simplepermission.SimplePermission
import kotlinx.android.synthetic.main.activity_permisssion.*

class SamplePermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisssion)

        btnCheckPermission.setOnClickListener {
            SimplePermission()
                .permissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                .askAgain()
                .callback(
                    onPermissionGranted = {
                        toast("Permission granted....")
                    },
                    onPermissionDeny = {
                        toast("Permission deny....")
                    })
                .request(this)
        }
    }
}
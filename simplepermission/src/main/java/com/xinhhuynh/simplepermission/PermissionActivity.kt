package com.xinhhuynh.simplepermission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PermissionActivity : AppCompatActivity() {

    companion object {
        fun start(from: Activity) {
            val intent = Intent(from, PermissionActivity::class.java)
            from.startActivity(intent)
        }
    }

    private var permissionManager: PermissionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request = SimplePermission.request

        permissionManager = PermissionManager().apply {
            permissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)

            askAgain(
                askAgain = request.askAgain,
                onShowAskAgain = request.onShowAskAgain
            )

            callback(
                onPermissionGranted = {
                    request.onPermissionGranted?.invoke()

                    finish()
                },
                onPermissionDeny = {
                    request.onPermissionDeny?.invoke()

                    finish()
                }
            )

            request(this@PermissionActivity)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager?.handleResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        permissionManager?.onActivityResult(this, requestCode, resultCode, data)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        permissionManager = null
    }
}
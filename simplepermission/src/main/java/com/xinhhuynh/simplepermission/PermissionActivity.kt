package com.xinhhuynh.simplepermission

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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

            val permissions = request?.permissions ?: emptyArray()

            if (request?.isAnyPermission == true) {
                anyPermissions(*permissions)
            } else {
                permissions(*permissions)
            }

            askAgain(
                askAgain = request?.askAgain ?: { false },
                onShowAskAgain = {
                    request?.onShowAskAgain?.invoke(this@PermissionActivity)?.apply {
                        show()

                        setOnCancelListener {
                            request.onPermissionDeny?.invoke()

                            finish()
                        }
                    } ?: showDefaultDialogAskAgain(request?.onPermissionDeny)
                }

            )

            callback(
                onPermissionGranted = {
                    request?.onPermissionGranted?.invoke()

                    finish()
                },
                onPermissionDeny = {
                    request?.onPermissionDeny?.invoke()

                    finish()
                }
            )

            request(this@PermissionActivity)
        }
    }

    private fun showDefaultDialogAskAgain(onPermissionDeny: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.sp_title_permission_deny))
            .setMessage(getString(R.string.sp_title_permission_msg))
            .setPositiveButton(getString(R.string.sp_title_permission_ok)) { _: DialogInterface, _: Int ->
                openPermissionSetting()
            }
            .setOnCancelListener {
                onPermissionDeny?.invoke()
                finish()
            }
            .create()
            .show()
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

        permissionManager?.onActivityResult(this, requestCode, resultCode)
    }

    override fun onDestroy() {
        super.onDestroy()

        permissionManager = null
        SimplePermission.request = null
    }
}
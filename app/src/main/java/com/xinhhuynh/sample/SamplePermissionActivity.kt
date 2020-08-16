package com.xinhhuynh.sample

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xinhhuynh.simpledialog.SimpleDialog
import com.xinhhuynh.simplepermission.SimplePermission
import com.xinhhuynh.simplepermission.openPermissionSetting
import kotlinx.android.synthetic.main.activity_permisssion.*

class SamplePermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisssion)

        btnCheckAny.setOnClickListener {
            SimplePermission()
                .anyPermissions(Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS)
                .callback(
                    onPermissionGranted = {
                        toast("Permission granted....")
                    },
                    onPermissionDeny = {
                        toast("Permission deny....")
                    })
                .request(this)
        }

        btnCheckAll.setOnClickListener {
            SimplePermission()
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                .callback(
                    onPermissionGranted = {
                        toast("Permission granted....")
                    },
                    onPermissionDeny = {
                        toast("Permission deny....")
                    })
                .request(this)
        }

        btnAskAgain.setOnClickListener {
            SimplePermission()
                .permissions(Manifest.permission.RECORD_AUDIO)
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

        btnCustomAskAgain.setOnClickListener {
            SimplePermission()
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .askAgain(onShowAskAgain = { activity ->
                    SimpleDialog(
                        context = activity,
                        title = { "Permission denied" },
                        msg = { "You need to allow permission to use this feature" },
                        textPositive = { "Ok" },
                        onClickPositiveButton = {
                            activity.openPermissionSetting()
                        },
                        textNegative = { "Cancel" }
                    ).dialog

                })
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
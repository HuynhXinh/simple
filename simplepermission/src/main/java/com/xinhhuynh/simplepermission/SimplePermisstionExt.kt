package com.xinhhuynh.simplepermission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.isGranted(permission: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true

    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.openPermissionSetting() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${packageName}")
    )
    this.startActivityForResult(intent, PermissionManager.SETTING_PERMISSION_REQUEST_CODE)
}

fun Activity.isDeniedForever(permissions: Array<out String>): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false

    return permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
}
package com.xinhhuynh.simplepermission

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionManager {
    companion object {
        const val PERMISSION_REQUEST_CODE = 9999
        const val SETTING_PERMISSION_REQUEST_CODE = 8888
    }

    private var permissions: Array<out String>? = null
    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDeny: (() -> Unit)? = null

    private var isAnyPermission = false

    private var askAgain = false
    private var onShowAskAgain: (() -> Unit)? = null

    fun anyPermissions(vararg permissions: String): PermissionManager {
        this.isAnyPermission = true
        this.permissions = permissions
        return this
    }

    fun permissions(vararg permissions: String): PermissionManager {
        this.permissions = permissions
        return this
    }

    fun askAgain(
        askAgain: (() -> Boolean) = { true },
        onShowAskAgain: (() -> Unit)? = null
    ): PermissionManager {
        this.askAgain = askAgain.invoke()
        this.onShowAskAgain = onShowAskAgain
        return this
    }

    fun callback(
        onPermissionGranted: (() -> Unit)? = null,
        onPermissionDeny: (() -> Unit)? = null
    ): PermissionManager {
        this.onPermissionGranted = onPermissionGranted
        this.onPermissionDeny = onPermissionDeny
        return this
    }

    fun request(fragment: Fragment) {
        val permissionToAsks = getPermissionToAsks(fragment.requireContext())

        if (permissionToAsks.isEmpty()) {
            onPermissionGranted?.invoke()
        } else {
            fragment.requestPermissions(permissionToAsks, PERMISSION_REQUEST_CODE)
        }
    }

    fun request(activity: Activity) {
        val permissionToAsks = getPermissionToAsks(activity)

        if (permissionToAsks.isEmpty()) {
            onPermissionGranted?.invoke()
        } else {
            ActivityCompat.requestPermissions(activity, permissionToAsks, PERMISSION_REQUEST_CODE)
        }
    }

    private fun getPermissionToAsks(context: Context): Array<String> {
        return permissions
            ?.filter { !context.isGranted(it) }
            ?.toTypedArray() ?: emptyArray()
    }

    fun handleResult(
        fragment: Fragment,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        handleResult(fragment.requireActivity(), requestCode, permissions, grantResults)
    }

    fun handleResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CODE) return

        if (isAnyPermission && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionGranted?.invoke()
        } else if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionGranted?.invoke()
        } else if (askAgain && activity.isDeniedForever(permissions)) {
            onShowAskAgain?.invoke() ?: showDialogConfirmOpenSetting(activity)
        } else {
            onPermissionDeny?.invoke()
        }
    }

    fun onActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != SETTING_PERMISSION_REQUEST_CODE) return

        if (isAnyPermission && permissions?.any { context.isGranted(it) } == true) {
            onPermissionGranted?.invoke()
        } else if (permissions.isNullOrEmpty() || permissions?.all { context.isGranted(it) } == true) {
            onPermissionGranted?.invoke()
        } else {
            onPermissionDeny?.invoke()
        }
    }

    private fun showDialogConfirmOpenSetting(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Permission denied")
            .setMessage("You need to allow permission to use this feature")
            .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                activity.openPermissionSetting()
            }
            .create()
            .show()
    }

}
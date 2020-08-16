package com.xinhhuynh.simplepermission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionManager {
    companion object {
        const val PERMISSION_REQUEST_CODE = 9999
        const val SETTING_PERMISSION_REQUEST_CODE = 8888

        private const val SHARED_PREFERENCE_NAME =
            "__permission__manager__share__preference__name__"

        private const val ASK_AGAIN_KEY_NAME = "__ask__again__key__name__"
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
        askAgain: (() -> Boolean)? = null,
        onShowAskAgain: (() -> Unit)? = null
    ): PermissionManager {
        this.askAgain = askAgain?.invoke() == true
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
        if (isAnyPermission && anyPermissionGranted(fragment.requireContext())) {
            onPermissionGranted?.invoke()
            return
        }

        val permissionToAsks = getPermissionToAsks(fragment.requireContext())

        if (permissionToAsks.isEmpty()) {
            onPermissionGranted?.invoke()
        } else {
            fragment.requestPermissions(permissionToAsks, PERMISSION_REQUEST_CODE)
        }
    }

    fun request(activity: Activity) {
        if (isAnyPermission && anyPermissionGranted(activity)) {
            onPermissionGranted?.invoke()
            return
        }

        val permissionToAsks = getPermissionToAsks(activity)

        if (permissionToAsks.isEmpty()) {
            onPermissionGranted?.invoke()
        } else {
            ActivityCompat.requestPermissions(activity, permissionToAsks, PERMISSION_REQUEST_CODE)
        }
    }

    private fun anyPermissionGranted(context: Context): Boolean {
        return permissions?.any { context.isGranted(it) } ?: false
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
        } else if (askAgain && isDeniedForever(activity, permissions)) {
            onShowAskAgain?.invoke()
        } else {
            onPermissionDeny?.invoke()
        }
    }

    private fun isDeniedForever(activity: Activity, permissions: Array<out String>): Boolean {
        val pref = activity.getSharedPreferences(
            SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

        val askAgain = pref.getBoolean(keyOf(permissions), false)

        if (askAgain) return true

        val isDeniedForever = activity.isDeniedForever(permissions)

        if (isDeniedForever) {
            pref.edit().putBoolean(keyOf(permissions), true).apply()
            return false
        }

        return askAgain
    }

    private fun keyOf(permissions: Array<out String>): String {
        return ASK_AGAIN_KEY_NAME + permissions.joinToString()
    }

    fun onActivityResult(context: Context, requestCode: Int, resultCode: Int) {
        if (requestCode != SETTING_PERMISSION_REQUEST_CODE) return

        if (isAnyPermission && permissions?.any { context.isGranted(it) } == true) {
            onPermissionGranted?.invoke()
        } else if (permissions.isNullOrEmpty() || permissions?.all { context.isGranted(it) } == true) {
            onPermissionGranted?.invoke()
        } else {
            onPermissionDeny?.invoke()
        }
    }
}
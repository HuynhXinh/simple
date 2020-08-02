package com.xinhhuynh.simplepermission

import android.app.Activity

class SimplePermission {

    companion object {
        val request = SimplePermissionRequest()
    }

    fun permissions(vararg permissions: String): SimplePermission {
        request.permissions = permissions
        return this
    }

    fun askAgain(
        askAgain: (() -> Boolean) = { true },
        onShowAskAgain: (() -> Unit)? = null
    ): SimplePermission {

        request.askAgain = askAgain
        request.onShowAskAgain = onShowAskAgain

        return this
    }

    fun callback(
        onPermissionGranted: (() -> Unit)? = null,
        onPermissionDeny: (() -> Unit)? = null
    ): SimplePermission {

        request.onPermissionGranted = onPermissionGranted
        request.onPermissionDeny = onPermissionDeny

        return this
    }

    fun request(activity: Activity) {
        PermissionActivity.start(activity)
    }
}
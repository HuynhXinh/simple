package com.xinhhuynh.simplepermission

import android.app.Activity
import android.app.Dialog
import android.content.Context

class SimplePermission {

    companion object {
        var request: SimplePermissionRequest? = null
    }

    private fun getRequest(): SimplePermissionRequest {
        if (request == null) {
            request = SimplePermissionRequest()
        }

        return request!!
    }

    fun anyPermissions(vararg permissions: String): SimplePermission {
        getRequest().isAnyPermission = true
        getRequest().permissions = permissions
        return this
    }

    fun permissions(vararg permissions: String): SimplePermission {
        getRequest().permissions = permissions
        return this
    }

    fun askAgain(
        askAgain: (() -> Boolean) = { true },
        onShowAskAgain: ((Activity) -> Dialog)? = null
    ): SimplePermission {

        getRequest().askAgain = askAgain
        getRequest().onShowAskAgain = onShowAskAgain

        return this
    }

    fun callback(
        onPermissionGranted: (() -> Unit)? = null,
        onPermissionDeny: (() -> Unit)? = null
    ): SimplePermission {

        getRequest().onPermissionGranted = onPermissionGranted
        getRequest().onPermissionDeny = onPermissionDeny

        return this
    }

    fun request(activity: Activity) {
        PermissionActivity.start(activity)
    }
}
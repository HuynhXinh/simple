package com.xinhhuynh.simplepermission

import android.app.Activity
import android.app.Dialog
import android.content.Context

class SimplePermissionRequest(
    var permissions: Array<out String> = emptyArray(),
    var isAnyPermission: Boolean = false,
    var askAgain: (() -> Boolean) = { true },
    var onShowAskAgain: ((Activity) -> Dialog)? = null,
    var onPermissionGranted: (() -> Unit)? = null,
    var onPermissionDeny: (() -> Unit)? = null
)
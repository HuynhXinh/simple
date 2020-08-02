package com.xinhhuynh.simplepermission

class SimplePermissionRequest(
    var permissions: Array<out String> = emptyArray(),
    var askAgain: (() -> Boolean) = { true },
    var onShowAskAgain: (() -> Unit)? = null,
    var onPermissionGranted: (() -> Unit)? = null,
    var onPermissionDeny: (() -> Unit)? = null
)
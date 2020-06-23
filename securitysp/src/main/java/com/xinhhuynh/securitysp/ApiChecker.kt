package com.xinhhuynh.securitysp

import android.os.Build

fun isApiFromM(onFromM: () -> Unit, onLeesThanM: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        onFromM()
    } else {
        onLeesThanM()
    }
}
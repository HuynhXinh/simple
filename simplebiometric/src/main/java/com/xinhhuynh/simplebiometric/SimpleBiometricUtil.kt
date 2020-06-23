package com.xinhhuynh.simplebiometric

import android.content.Context
import androidx.biometric.BiometricManager

object SimpleBiometricUtil {
    fun isAvailable(context: Context): Boolean {
        return BiometricManager.from(context)
                .canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun simpleCheck(context: Context, onAvailable: (Boolean) -> Unit) {
        onAvailable(isAvailable(context))
    }

    fun simpleCheck(context: Context, onAvailable: () -> Unit, onNotAvailable: (() -> Unit)? = null) {
        if (isAvailable(context)) {
            onAvailable()
        } else {
            onNotAvailable?.invoke()
        }
    }

    fun check(context: Context, callback: (State) -> Unit) {
        when (BiometricManager.from(context).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                /**
                 *  App can authenticate using biometrics.
                 */
                callback(State.Available)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                /**
                 * No biometric features available on this device.
                 */
                callback(State.UnAvailable)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                /**
                 * Biometric features are currently unavailable.
                 */
                callback(State.UnAvailable)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                /**
                 * The user hasn't associated any biometric credentials with their account.
                 */
                callback(State.NoneEnrolled)
            }
        }
    }

    sealed class State {
        object Available : State()
        object UnAvailable : State()
        object NoneEnrolled : State()
    }
}
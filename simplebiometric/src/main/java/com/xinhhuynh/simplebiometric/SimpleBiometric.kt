package com.xinhhuynh.simplebiometric

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class SimpleBiometric(
        private val activity: FragmentActivity,
        private val title: (() -> CharSequence)? = null,
        private val subTitle: (() -> CharSequence)? = null,
        private val buttonText: (() -> CharSequence)? = null,
        private val deviceCredentialAllowed: (() -> Boolean)? = null
) {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var _callback: ((AuthenticateState) -> Unit)? = null

    fun callback(callback: ((AuthenticateState) -> Unit)): SimpleBiometric {
        _callback = callback
        return this
    }

    fun simpleCallback(
            onSuccess: () -> Unit,
            onFail: (() -> Unit)? = null,
            onCancel: ((msg: CharSequence) -> Unit)? = null
    ): SimpleBiometric {
        _callback = {
            when (it) {
                is AuthenticateState.Success -> onSuccess()

                is AuthenticateState.Fail -> onFail?.invoke()

                is AuthenticateState.Error -> {
                    onCancel?.invoke(it.msg)
                }
            }
        }
        return this
    }

    fun show() {
        if (buttonText != null && deviceCredentialAllowed != null) {
            throw IllegalArgumentException("Can't have both negative button behavior and device credential enabled")
        }

        biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)

                        _callback?.invoke(AuthenticateState.Error(errorCode, errString))
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        _callback?.invoke(AuthenticateState.Success)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        _callback?.invoke(AuthenticateState.Fail)
                    }
                })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
                .apply {

                    title?.invoke()?.let {
                        setTitle(it)
                    }

                    subTitle?.invoke()?.let {
                        setSubtitle(it)
                    }

                    buttonText?.invoke()?.let {
                        setNegativeButtonText(it)
                    }

                    deviceCredentialAllowed?.invoke()?.let {
                        setDeviceCredentialAllowed(it)
                    }

                }.build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun cancel() {
        biometricPrompt.cancelAuthentication()
    }

    sealed class AuthenticateState {
        object Success : AuthenticateState()

        /**
         * Code base on this: androidx.biometric.BiometricConstants
         */
        class Error(val code: Int, val msg: CharSequence) : AuthenticateState()

        object Fail : AuthenticateState()
    }
}
package com.xinhhuynh.securitysp

import android.content.Context

interface EncryptDecrypt {
    fun encrypt(text: String): String

    fun decrypt(encrypted: String): String
}

class EncryptDecryptBuilder(context: Context) {
    companion object {
        private const val KEY_STORE_ALIAS = "__key__store__alias__"
    }

    private val keyStoreLessManager: KeyStoreManager
    private val cipherManager: CipherManager

    init {
        keyStoreLessManager = KeyStoreManager(
            context = context,
            alias = KEY_STORE_ALIAS
        )
        keyStoreLessManager.init()

        cipherManager = CipherManager(keyStoreLessManager.getSecretKey())
    }

    fun build(): EncryptDecrypt {
        return object : EncryptDecrypt {
            override fun encrypt(text: String): String {
                return cipherManager.encrypt(text)
            }

            override fun decrypt(encrypted: String): String {
                return cipherManager.decrypt(encrypted)
            }
        }
    }
}
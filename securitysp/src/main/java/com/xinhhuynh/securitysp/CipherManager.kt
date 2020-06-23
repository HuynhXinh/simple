package com.xinhhuynh.securitysp

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import kotlin.text.Charsets.UTF_8

class CipherManager(private val secretKey: Key) {
    companion object {
        private const val AES_MODE_M_OR_GREATER = "AES/GCM/NoPadding"
        private const val GCM_PARAMETER_SPEC_LENGTH = 128

        private val FIXED_IV = byteArrayOf(
            55, 54, 53, 52, 51, 50,
            49, 48, 47,
            46, 45, 44
        )

        private const val AES_MODE_LESS_THAN_M = "AES/ECB/PKCS7Padding"
        private const val CIPHER_PROVIDER_NAME_ENCRYPTION_DECRYPTION_AES = "BC"
    }

    private lateinit var cipher: Cipher

    init {
        isApiFromM(
            onFromM = {
                cipher = Cipher.getInstance(AES_MODE_M_OR_GREATER)
            },
            onLeesThanM = {
                cipher = Cipher.getInstance(
                    AES_MODE_LESS_THAN_M,
                    CIPHER_PROVIDER_NAME_ENCRYPTION_DECRYPTION_AES
                )
            })
    }

    private fun Cipher.initCompat(mode: Int, secretKey: Key) {
        isApiFromM(
            onFromM = {
                this.init(
                    mode,
                    secretKey,
                    GCMParameterSpec(
                        GCM_PARAMETER_SPEC_LENGTH,
                        FIXED_IV
                    )
                )
            },
            onLeesThanM = {
                this.init(mode, secretKey)
            })
    }

    fun encrypt(planText: String): String {
        cipher.initCompat(Cipher.ENCRYPT_MODE, secretKey)

        val encodedBytes =
            cipher.doFinal(planText.toByteArray())
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    fun decrypt(encrypted: String): String {
        cipher.initCompat(Cipher.DECRYPT_MODE, secretKey)

        val encryptedDecodedData: ByteArray =
            Base64.decode(encrypted, Base64.DEFAULT)

        val decodedBytes: ByteArray = cipher.doFinal(encryptedDecodedData)
        return String(decodedBytes, UTF_8)
    }
}
package com.xinhhuynh.securitysp

import android.content.Context
import android.util.Base64
import com.xinhhuynh.securitysp.exception.SecretKeyNullPointException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

class SecretKeyCreatorV18(
    context: Context,
    private val privateKeyEntry: KeyStore.PrivateKeyEntry
) {

    companion object {
        private const val SHARED_PREFERENCE_NAME =
            "__secret__key__manager__share__preference__name__"
        private const val ENCRYPTED_KEY_NAME = "__secret__key__manager__key__name__"

        private const val RSA_MODE = "RSA/ECB/PKCS1Padding"

        private const val CIPHER_PROVIDER_NAME_ENCRYPTION_DECRYPTION_RSA = "AndroidOpenSSL"
        private const val SECRET_KEY_SPEC_ALGORITHM = "AES"
        private const val KEY_SIZE = 16
    }

    private val pref = context.getSharedPreferences(
        SHARED_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    private val cipher = Cipher.getInstance(
        RSA_MODE,
        CIPHER_PROVIDER_NAME_ENCRYPTION_DECRYPTION_RSA
    )

    @Throws(
        CertificateException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        UnrecoverableEntryException::class,
        IOException::class
    )
    fun generateKey(): Boolean {
        if (isKeyExisted()) return true

        val key = ByteArray(KEY_SIZE)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(key)

        val encryptedKey: ByteArray = rsaEncrypt(key)

        val encryptedKeyBase64encoded =
            Base64.encodeToString(encryptedKey, Base64.DEFAULT)

        return pref.edit()
            .putString(ENCRYPTED_KEY_NAME, encryptedKeyBase64encoded)
            .commit()
    }

    private fun getRawKey(): String? {
        return pref.getString(ENCRYPTED_KEY_NAME, null)
    }

    private fun isKeyExisted(): Boolean {
        return getRawKey() != null
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        UnrecoverableEntryException::class,
        InvalidKeyException::class
    )
    private fun rsaEncrypt(secret: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, cipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()

        return outputStream.toByteArray()
    }

    fun clear(): Boolean {
        return pref.edit().clear().commit()
    }

    fun getKey(): Key {
        val encryptedKeyBase64Encoded = getRawKey() ?: throw SecretKeyNullPointException()

        val encrypted = Base64.decode(encryptedKeyBase64Encoded, Base64.DEFAULT)

        val key: ByteArray = rsaDecrypt(encrypted)

        return SecretKeySpec(key,
            SECRET_KEY_SPEC_ALGORITHM
        )
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class,
        UnrecoverableEntryException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class
    )
    private fun rsaDecrypt(encrypted: ByteArray): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        val cipherInputStream = CipherInputStream(ByteArrayInputStream(encrypted), cipher)

        val values = ArrayList<Byte>()
        var nextByte: Int
        while (cipherInputStream.read().also { nextByte = it } != -1) {
            values.add(nextByte.toByte())
        }
        val decryptedKeyAsBytes = ByteArray(values.size)
        for (i in decryptedKeyAsBytes.indices) {
            decryptedKeyAsBytes[i] = values[i]
        }
        return decryptedKeyAsBytes
    }
}
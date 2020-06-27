package com.xinhhuynh.securitysp

import android.content.Context
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
import android.os.Build.VERSION_CODES.M
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.xinhhuynh.securitysp.exception.KeyStoreInValidException
import com.xinhhuynh.securitysp.exception.SecretKeyNullPointException
import java.math.BigInteger
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

class KeyStoreManager(
    private val context: Context,
    private val alias: String
) {

    companion object {
        private const val RSA_ALGORITHM_NAME = "RSA"
        private const val ANDROID_KEY_STORE_NAME = "AndroidKeyStore"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_NAME)

    fun init() {
        keyStore.load(null)
        if (!isValidKey()) {
            initKeyStore()
        }
    }

    private fun isValidKey(): Boolean {
        return keyStore.containsAlias(alias)
    }

    private fun initKeyStore() {
        InitKeyStore(context = context, alias = alias).init()
    }

    private fun clear() {
        keyStore.deleteEntry(alias)
    }

    fun reInit() {
        clear()
        init()
    }

    fun getSecretKey(): Key {
        return SecretKeyGetter().get()
    }

    private inner class SecretKeyGetter {
        fun get(): Key {
            var key: Key? = null

            isApiFromM(
                onFromM = {
                    key = keyStore.getKey(alias, null)
                },
                onLeesThanM = {
                    key = SecretKeyCreatorV18(
                        context = context,
                        privateKeyEntry = getPrivateKey()
                    ).getKey()
                })

            return key ?: throw SecretKeyNullPointException()
        }
    }

    private fun getPrivateKey(): KeyStore.PrivateKeyEntry {
        return (keyStore.getEntry(alias, null) as? KeyStore.PrivateKeyEntry)
            ?: throw KeyStoreInValidException()
    }

    private inner class InitKeyStore(private val context: Context, private val alias: String) {

        fun init() {
            isApiFromM(
                onFromM = {
                    initFromM()
                },
                onLeesThanM = {
                    initLessThanM()
                })
        }

        @RequiresApi(M)
        private fun initFromM() {
            val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE_NAME
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE) // NOTE no Random IV. According to above this is less secure but acceptably so.
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )
            // Note according to [docs](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.html)
            // this generation will also add it to the keystore.
            // Note according to [docs](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.html)
            // this generation will also add it to the keystore.
            keyGenerator.generateKey()
        }

        @RequiresApi(JELLY_BEAN_MR2)
        private fun initLessThanM() {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 30)
            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(X500Principal("CN=$alias"))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
            val kpg = KeyPairGenerator.getInstance(
                RSA_ALGORITHM_NAME,
                ANDROID_KEY_STORE_NAME
            )
            kpg.initialize(spec)
            kpg.generateKeyPair()

            SecretKeyCreatorV18(
                context = context,
                privateKeyEntry = getPrivateKey()
            ).apply {
                try {
                    generateKey()
                } catch (ex: Exception) {
                    clear()
                    reInit()
                }
            }
        }
    }
}
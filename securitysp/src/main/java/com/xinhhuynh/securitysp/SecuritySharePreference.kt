package com.xinhhuynh.securitysp

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Base64
import android.util.Pair
import androidx.collection.ArraySet
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.text.Charsets.UTF_8

open class SecuritySharePreference(context: Context, fileName: String) : SharedPreferences {
    companion object {
        private const val NULL_VALUE = "__NULL__"
    }

    private val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    val listeners = mutableListOf<OnSharedPreferenceChangeListener>()

    private val encryptDecrypt = EncryptDecryptBuilder(context).build()


    private fun String.encrypt(): String {
        return encryptDecrypt.encrypt(this)
    }

    private fun String.decrypt(): String {
        return encryptDecrypt.decrypt(this)
    }

    override fun contains(key: String): Boolean {
        if (isReservedKey(key)) {
            throw SecurityException("$key is a reserved key for the encryption keyset.")
        }
        return sharedPreferences.contains(key.encrypt())
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val value = getDecryptedObject(key)
        return if (value != null && value is Boolean) value else defValue
    }

    override fun getInt(key: String, defValue: Int): Int {
        val value = getDecryptedObject(key)
        return if (value != null && value is Int) value else defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        val value = getDecryptedObject(key)
        return if (value != null && value is Long) value else defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val value = getDecryptedObject(key)
        return if (value != null && value is Float) value else defValue
    }

    override fun getString(key: String, defValue: String?): String? {
        val value = getDecryptedObject(key)
        return if (value != null && value is String) value else defValue
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String> {
        val returnValues: ArraySet<String>
        val value = getDecryptedObject(key)
        returnValues = value as? ArraySet<String> ?: ArraySet()

        return if (returnValues.isNotEmpty()) returnValues else defValues!!
    }

    override fun getAll(): MutableMap<String, *> {
        val allEntries: MutableMap<String, in Any?> = HashMap()
        sharedPreferences.all.entries.map { it.key }.forEach { key ->
            if (!isReservedKey(key)) {
                val decryptedKey: String = key.encrypt()
                allEntries[decryptedKey] = getDecryptedObject(decryptedKey)
            }
        }
        return allEntries
    }

    override fun edit(): SharedPreferences.Editor {
        return Editor(this, sharedPreferences.edit())
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.remove(listener)
    }

    private fun getDecryptedObject(key: String): Any? {
        if (isReservedKey(key)) {
            throw SecurityException("$key is a reserved key for the encryption keyset.")
        }

        var returnValue: Any? = null
        try {
            val encryptedValue = sharedPreferences.getString(key.encrypt(), null)?.decrypt()
            if (encryptedValue != null) {
                val value = Base64.decode(encryptedValue, Base64.DEFAULT)
                val buffer = ByteBuffer.wrap(value)
                buffer.position(0)
                val typeId = buffer.int
                val type =
                    EncryptedType.fromId(
                        typeId
                    )

                returnValue = when (type) {
                    EncryptedType.STRING -> {

                        val stringLength = buffer.int
                        val stringSlice = buffer.slice()
                        buffer.limit(stringLength)
                        val stringValue = UTF_8.decode(stringSlice).toString()

                        if (stringValue == NULL_VALUE) {
                            null
                        } else {
                            stringValue
                        }
                    }
                    EncryptedType.INT -> buffer.int

                    EncryptedType.LONG -> buffer.long

                    EncryptedType.FLOAT -> buffer.float

                    EncryptedType.BOOLEAN -> buffer.get() != 0.toByte()

                    EncryptedType.STRING_SET -> {
                        val stringSet = ArraySet<String>()
                        while (buffer.hasRemaining()) {
                            val subStringLength = buffer.int
                            val subStringSlice = buffer.slice()
                            subStringSlice.limit(subStringLength)
                            buffer.position(buffer.position() + subStringLength)
                            stringSet.add(UTF_8.decode(subStringSlice).toString())
                        }
                        if (stringSet.size == 1 && NULL_VALUE == stringSet.valueAt(0)) {
                            null
                        } else {
                            stringSet
                        }
                    }
                    else -> null
                }
            }
        } catch (ex: GeneralSecurityException) {
            throw SecurityException("Could not decrypt value. " + ex.message, ex)
        }
        return returnValue
    }

    private enum class EncryptedType(val id: Int) {
        STRING(0),
        STRING_SET(1),
        INT(2),
        LONG(3),
        FLOAT(4),
        BOOLEAN(5);

        companion object {
            fun fromId(id: Int): EncryptedType? {
                when (id) {
                    0 -> return STRING
                    1 -> return STRING_SET
                    2 -> return INT
                    3 -> return LONG
                    4 -> return FLOAT
                    5 -> return BOOLEAN
                }
                return null
            }
        }
    }

    private inner class Editor(
        private val keyStoreSharePreference: SecuritySharePreference,
        private val editor: SharedPreferences.Editor
    ) : SharedPreferences.Editor {

        private val clearRequested = AtomicBoolean(false)
        private val keysChanged = CopyOnWriteArrayList<String>()

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            val stringBytes = value.orValueDefault().toByteArray(UTF_8)
            val stringByteLength = stringBytes.size

            val buffer = ByteBuffer.allocate(
                    Int.SIZE_BYTES + Int.SIZE_BYTES
                            + stringByteLength
            )
            buffer.putInt(EncryptedType.STRING.id)
            buffer.putInt(stringByteLength)
            buffer.put(stringBytes)

            putEncryptedObject(key, buffer.array())
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            val buffer =
                    ByteBuffer.allocate(Int.SIZE_BYTES + Long.SIZE_BYTES)
            buffer.putInt(EncryptedType.LONG.id)
            buffer.putLong(value)

            putEncryptedObject(key, buffer.array())
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            val buffer =
                    ByteBuffer.allocate(Int.SIZE_BYTES + Int.SIZE_BYTES)
            buffer.putInt(EncryptedType.INT.id)
            buffer.putInt(value)

            putEncryptedObject(key, buffer.array())
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            val buffer =
                    ByteBuffer.allocate(Int.SIZE_BYTES + Byte.SIZE_BYTES)
            buffer.putInt(EncryptedType.BOOLEAN.id)
            buffer.put(if (value) 1.toByte() else 0.toByte())

            putEncryptedObject(key, buffer.array())
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            val buffer =
                    ByteBuffer.allocate(Int.SIZE_BYTES + Int.SIZE_BYTES)
            buffer.putInt(EncryptedType.FLOAT.id)
            buffer.putFloat(value)

            putEncryptedObject(key, buffer.array())
            return this
        }

        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor {
            val newValues = values ?: ArraySet<String>().apply { add(NULL_VALUE) }

            val byteValues: MutableList<ByteArray> =
                    ArrayList(newValues.size)
            var totalBytes = newValues.size * Int.SIZE_BYTES
            for (strValue in newValues) {
                val byteValue = strValue.toByteArray(UTF_8)
                byteValues.add(byteValue)
                totalBytes += byteValue.size
            }
            totalBytes += Int.SIZE_BYTES
            val buffer = ByteBuffer.allocate(totalBytes)

            buffer.putInt(EncryptedType.STRING_SET.id)
            for (bytes in byteValues) {
                buffer.putInt(bytes.size)
                buffer.put(bytes)
            }

            putEncryptedObject(key, buffer.array())
            return this
        }

        override fun commit(): Boolean {
            clearKeysIfNeeded()
            return try {
                editor.commit()
            } finally {
                notifyListeners()
                keysChanged.clear()
            }
        }

        override fun apply() {
            clearKeysIfNeeded()
            editor.apply()
            notifyListeners()
        }

        override fun remove(key: String): SharedPreferences.Editor {
            if (keyStoreSharePreference.isReservedKey(key)) {
                throw SecurityException("$key is a reserved key for the encryption keyset.")
            }
            editor.remove(key.encrypt())
            keysChanged.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            // Set the flag to clear on commit, this operation happens first on commit.
            // Cannot use underlying clear operation, it will remove the keysets and
            // break the editor.

            // Set the flag to clear on commit, this operation happens first on commit.
            // Cannot use underlying clear operation, it will remove the keysets and
            // break the editor.
            clearRequested.set(true)
            return this
        }

        private fun clearKeysIfNeeded() {
            // Call "clear" first as per the documentation, remove all keys that haven't
            // been modified in this editor.
            if (clearRequested.getAndSet(false)) {
                for (key in keyStoreSharePreference.all.keys) {
                    if (!keysChanged.contains(key)
                            && !keyStoreSharePreference.isReservedKey(key)
                    ) {
                        editor.remove(key.encrypt())
                    }
                }
            }
        }

        private fun putEncryptedObject(key: String, value: ByteArray) {
            if (keyStoreSharePreference.isReservedKey(key)) {
                throw SecurityException("$key is a reserved key for the encryption keyset.")
            }

            keysChanged.add(key)

            try {
                val encryptedPair: Pair<String, String> =
                        keyStoreSharePreference.encryptKeyValuePair(key, value)
                editor.putString(encryptedPair.first, encryptedPair.second)
            } catch (ex: GeneralSecurityException) {
                throw SecurityException("Could not encrypt data: " + ex.message, ex)
            }
        }

        private fun notifyListeners() {
            for (listener in keyStoreSharePreference.listeners) {
                for (key in keysChanged) {
                    listener.onSharedPreferenceChanged(keyStoreSharePreference, key)
                }
            }
        }
    }

    private fun encryptKeyValuePair(key: String, value: ByteArray): Pair<String, String> {
        val encryptedKey = key.encrypt()
        val encryptValue = Base64.encodeToString(value, Base64.DEFAULT).encrypt()

        return Pair(encryptedKey, encryptValue)
    }

    private fun isReservedKey(key: String): Boolean {
        return false
    }

    private fun String?.orValueDefault(): String {
        return this ?: NULL_VALUE
    }
}
package com.sentinel.wallet.storage

import android.content.Context
import android.content.SharedPreferences

class SecureKeyStore(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "sentinel_wallet_secure",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_PRIVATE = "wallet_private_key"
        private const val KEY_PUBLIC = "wallet_public_key"
    }

    /**
     * Speichert die Wallet-Schlüssel
     */
    fun saveKeys(privateKey: String, publicKey: String) {
        prefs.edit().apply {
            putString(KEY_PRIVATE, privateKey)
            putString(KEY_PUBLIC, publicKey)
            apply()
        }
    }

    /**
     * Lädt den privaten Schlüssel
     * @return Privater Schlüssel als Hex-String oder null
     */
    fun getPrivateKey(): String? {
        return prefs.getString(KEY_PRIVATE, null)
    }

    /**
     * Lädt den öffentlichen Schlüssel
     * @return Öffentlicher Schlüssel als Hex-String oder null
     */
    fun getPublicKey(): String? {
        return prefs.getString(KEY_PUBLIC, null)
    }

    /**
     * Prüft ob Schlüssel existieren
     */
    fun hasKeys(): Boolean {
        return getPrivateKey() != null && getPublicKey() != null
    }

    /**
     * Löscht alle Schlüssel
     */
    fun clearKeys() {
        prefs.edit().apply {
            remove(KEY_PRIVATE)
            remove(KEY_PUBLIC)
            apply()
        }
    }
}
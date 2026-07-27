package com.sentinel.wallet.crypto

import android.util.Log
import com.goterl.lazysodium.LazySodiumAndroid
import com.goterl.lazysodium.SodiumAndroid
import com.goterl.lazysodium.utils.Key
import com.goterl.lazysodium.utils.KeyPair

object Ed25519Crypto {

    private const val TAG = "Ed25519Crypto"

    private val lazySodium = LazySodiumAndroid(SodiumAndroid())

    init {
        Log.d(TAG, "✅ LazySodium initialized")
    }

    fun generateKeyPair(): Pair<String, String> {
        try {
            val keyPair: KeyPair = lazySodium.cryptoSignKeypair()

            val publicKeyHex = keyPair.publicKey.asHexString
            val privateKeyHex = keyPair.secretKey.asHexString

            Log.d(TAG, "✅ Key pair generated")
            Log.d(TAG, "Public key length: ${publicKeyHex.length}")
            Log.d(TAG, "Private key length: ${privateKeyHex.length}")

            // IMPORTANT:
            // Repository expects Pair(privateKey, publicKey)
            return Pair(privateKeyHex, publicKeyHex)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to generate key pair: ${e.message}")
            throw e
        }
    }


    fun signData(privateKeyHex: String, data: ByteArray): String {
        return try {
            val privateKey = Key.fromHexString(privateKeyHex)

            val message = data.toString(Charsets.UTF_8)

            val signature = lazySodium.cryptoSignDetached(
                message,
                privateKey
            )

            Log.d(TAG, "✅ Data signed")

            signature

        } catch (e: Exception) {
            Log.e(TAG, "❌ Signing failed: ${e.message}")
            throw e
        }
    }


    fun verifySignature(
        publicKeyHex: String,
        data: ByteArray,
        signatureHex: String
    ): Boolean {

        return try {
            val publicKey = Key.fromHexString(publicKeyHex)

            val message = data.toString(Charsets.UTF_8)

            lazySodium.cryptoSignVerifyDetached(
                signatureHex,
                message,
                publicKey
            )

        } catch (e: Exception) {
            Log.e(TAG, "❌ Verification failed: ${e.message}")
            false
        }
    }


    fun encodePublicKey(publicKeyHex: String): String {
        return publicKeyHex
    }


    private fun ByteArray.toHexString(): String {
        return joinToString("") {
            "%02x".format(it)
        }
    }
}
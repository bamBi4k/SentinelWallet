package com.sentinel.wallet.crypto

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CryptoSelfTest {

    companion object {
        private const val TAG = "CryptoSelfTest"
    }


    @Test
    fun testEd25519CompleteFlow() {

        Log.d(TAG, "========== Sentinel Crypto Self Test ==========")


        /*
         * 1. Generate keypair
         */
        val (publicKey, privateKey) =
            Ed25519Crypto.generateKeyPair()


        Log.d(
            TAG,
            "Public Key: $publicKey"
        )

        Log.d(
            TAG,
            "Private Key length: ${privateKey.length}"
        )


        /*
         * Ed25519:
         * Public key = 32 bytes = 64 hex chars
         * Secret key = 64 bytes = 128 hex chars
         */
        assertTrue(
            "Public key length invalid",
            publicKey.length == 64
        )

        assertTrue(
            "Private key length invalid",
            privateKey.length == 128
        )


        /*
         * 2. Sign test message
         */
        val message =
            "Sentinel crypto self test".toByteArray()


        val signature =
            Ed25519Crypto.signData(
                privateKey,
                message
            )


        Log.d(
            TAG,
            "Signature: $signature"
        )


        /*
         * Ed25519 signature:
         * 64 bytes = 128 hex chars
         */
        assertTrue(
            "Signature length invalid",
            signature.length == 128
        )


        /*
         * 3. Verify signature
         */
        val verified =
            Ed25519Crypto.verifySignature(
                publicKey,
                message,
                signature
            )


        Log.d(
            TAG,
            "Verification result: $verified"
        )


        assertTrue(
            "Signature verification failed",
            verified
        )


        Log.d(
            TAG,
            "========== SUCCESS =========="
        )
    }
}
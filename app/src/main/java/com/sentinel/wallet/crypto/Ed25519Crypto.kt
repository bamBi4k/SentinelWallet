package com.sentinel.wallet.crypto

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.util.encoders.Hex
import java.security.SecureRandom

object Ed25519Crypto {

    private val secureRandom = SecureRandom()

    /**
     * Generiert ein Ed25519 Schlüsselpaar
     * @return Pair(privatKeyHex, publicKeyHex)
     */
    fun generateKeyPair(): Pair<String, String> {
        // Generiere 32 Byte privaten Schlüssel
        val privateKeyBytes = ByteArray(32)
        secureRandom.nextBytes(privateKeyBytes)

        // Erstelle private und public Key
        val privateKey = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
        val publicKey = privateKey.generatePublicKey()

        return Pair(
            Hex.toHexString(privateKeyBytes),
            Hex.toHexString(publicKey.encoded)
        )
    }

    /**
     * Signiert Daten mit Ed25519
     * @param privateKeyHex Privater Schlüssel als Hex-String
     * @param data Daten die signiert werden sollen
     * @return Signatur als Hex-String
     */
    fun signData(privateKeyHex: String, data: ByteArray): String {
        val privateKeyBytes = Hex.decode(privateKeyHex)
        val privateKey = Ed25519PrivateKeyParameters(privateKeyBytes, 0)

        val signer = Ed25519Signer()
        signer.init(true, privateKey)
        signer.update(data, 0, data.size)

        val signature = signer.generateSignature()
        return Hex.toHexString(signature)
    }

    /**
     * Verifiziert eine Signatur
     * @param publicKeyHex Öffentlicher Schlüssel als Hex-String
     * @param data Ursprüngliche Daten
     * @param signatureHex Signatur als Hex-String
     * @return true wenn Signatur gültig
     */
    fun verifySignature(publicKeyHex: String, data: ByteArray, signatureHex: String): Boolean {
        try {
            val publicKeyBytes = Hex.decode(publicKeyHex)
            val publicKey = Ed25519PublicKeyParameters(publicKeyBytes, 0)
            val signatureBytes = Hex.decode(signatureHex)

            val signer = Ed25519Signer()
            signer.init(false, publicKey)
            signer.update(data, 0, data.size)

            return signer.verifySignature(signatureBytes)
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Konvertiert einen öffentlichen Schlüssel für die Übertragung
     */
    fun encodePublicKey(publicKeyHex: String): String {
        return publicKeyHex
    }
}
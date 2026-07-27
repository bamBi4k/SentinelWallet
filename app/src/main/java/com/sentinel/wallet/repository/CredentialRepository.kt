package com.sentinel.wallet.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.sentinel.wallet.crypto.Ed25519Crypto
import com.sentinel.wallet.models.Claim
import com.sentinel.wallet.models.Credential
import com.sentinel.wallet.models.network.*
import com.sentinel.wallet.models.network.RetrofitClient
import com.sentinel.wallet.storage.SecureKeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class CredentialRepository(private val context: Context) {

    private val keyStore = SecureKeyStore(context)
    private val gson = Gson()

    fun ensureWalletKeys(): Pair<String, String> {
        val privateKey = keyStore.getPrivateKey()
        val publicKey = keyStore.getPublicKey()

        if (privateKey != null && publicKey != null) {
            // Prüfe, ob die Schlüssel gültig sind
            if (privateKey.length > 0 && publicKey.length > 0) {
                return Pair(privateKey, publicKey)
            }
            keyStore.clearKeys()
        }

        val (newPrivate, newPublic) = Ed25519Crypto.generateKeyPair()
        keyStore.saveKeys(newPrivate, newPublic)
        Log.d("CredentialRepo", "✅ Neue Schlüssel generiert und gespeichert")
        return Pair(newPrivate, newPublic)
    }

    suspend fun requestCredential(birthYear: Int): Result<Credential> = withContext(Dispatchers.IO) {
        try {
            val request = SetupRequest(birthYear)
            val response = RetrofitClient.instance.setupCredential(request)

            if (response.isSuccessful && response.body()?.status == "success") {
                val credentialData = response.body()?.credential
                if (credentialData != null) {
                    val claims = listOf(
                        Claim.ageOver18(credentialData.ageOver18),
                        Claim.euCitizen(verified = true),
                        Claim.humanVerified(verified = true),
                        Claim.governmentVerified(verified = true)
                    )

                    val credential = Credential(
                        userId = credentialData.userId,
                        issuer = credentialData.issuer,
                        issuedAt = credentialData.issuedAt,
                        claims = claims,
                        signature = credentialData.signature,
                        isVerified = credentialData.ageOver18
                    )
                    return@withContext Result.success(credential)
                }
            }
            return@withContext Result.failure(Exception("Credential request failed"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun createChallenge(): Result<ChallengeResponse> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.createChallenge()
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!)
            }
            return@withContext Result.failure(Exception("Challenge creation failed"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun generateLocalProof(
        challenge: String,
        claimType: String,
        credential: Credential
    ): ProofData? {
        return withContext(Dispatchers.IO) {
            try {
                val (privateKey, publicKey) = ensureWalletKeys()

                // Prüfe, ob die Schlüssel gültig sind (64 Zeichen Hex)
                if (privateKey.length != 128 || publicKey.length != 64) {
                    Log.e(
                        "CredentialRepo",
                        "❌ Ungültige Schlüssellänge: private=${privateKey.length}, public=${publicKey.length}"
                    )
                    return@withContext null
                }

                // Daten für Signatur vorbereiten (OHNE signature Feld)
                val proofData: Map<String, Any> = mapOf(
                    "claim_type" to claimType,
                    "value" to credential.isVerified,
                    "challenge" to challenge,
                    "timestamp" to Instant.now().toString(),
                    "public_key" to publicKey
                )

                val proofJson = canonicalJson(proofData)

                Log.d("SENTINEL_DEBUG", "========== SIGNED JSON ==========")
                Log.d("SENTINEL_DEBUG", proofJson)
                Log.d("SENTINEL_DEBUG", "==================================")

                val dataBytes = proofJson.toByteArray(Charsets.UTF_8)

                // Signatur generieren
                val signature = Ed25519Crypto.signData(privateKey, dataBytes)

                ProofData(
                    claimType = claimType,
                    value = credential.isVerified,
                    challenge = challenge,
                    timestamp = proofData["timestamp"] as String,
                    signature = signature,
                    publicKey = publicKey
                )
            } catch (e: Exception) {
                Log.e("CredentialRepo", "❌ Fehler bei Proof-Generierung", e)
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    // ============================================
    // VERIFICATION
    // ============================================

    suspend fun verifyProof(sessionId: String, proof: ProofData): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = VerifyRequest(sessionId, proof)
            val response = RetrofitClient.instance.verifyProof(request)

            if (response.isSuccessful && response.body()?.status == "success") {
                return@withContext Result.success(true)
            }
            return@withContext Result.failure(Exception("Verification failed: ${response.body()?.message}"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun verifyQrProof(sessionId: String, proof: ProofData): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = VerifyRequest(sessionId, proof)
            val response = RetrofitClient.instance.verifyQrProof(request)

            if (response.isSuccessful && response.body()?.status == "success") {
                return@withContext Result.success(true)
            }
            return@withContext Result.failure(Exception("QR verification failed: ${response.body()?.message}"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    private fun canonicalJson(data: Map<String, Any>): String {
        return data.toSortedMap()
            .map { (key, value) ->
                "\"$key\":${when(value) {
                    is String -> "\"$value\""
                    is Boolean -> value.toString()
                    else -> value.toString()
                }}"
            }
            .joinToString(
                separator = ",",
                prefix = "{",
                postfix = "}"
            )
    }
}
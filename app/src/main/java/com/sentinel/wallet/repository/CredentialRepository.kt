package com.sentinel.wallet.repository

import android.content.Context
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
            return Pair(privateKey, publicKey)
        }

        val (newPrivate, newPublic) = Ed25519Crypto.generateKeyPair()
        keyStore.saveKeys(newPrivate, newPublic)
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

                val proofData = mapOf(
                    "claim_type" to claimType,
                    "value" to credential.isVerified,
                    "challenge" to challenge,
                    "timestamp" to Instant.now().toString(),
                    "public_key" to publicKey
                )

                val proofJson = gson.toJson(proofData)
                val signature = Ed25519Crypto.signData(privateKey, proofJson.toByteArray())

                ProofData(
                    claimType = claimType,
                    value = credential.isVerified,
                    challenge = challenge,
                    timestamp = proofData["timestamp"] as String,
                    signature = signature,
                    publicKey = publicKey
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

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
}
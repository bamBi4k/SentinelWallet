package com.sentinel.wallet.repository

import com.sentinel.wallet.models.Credential
import com.sentinel.wallet.models.Claim
import com.sentinel.wallet.models.network.*
import com.sentinel.wallet.models.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CredentialRepository {

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

    suspend fun generateProof(challenge: String, claimType: String): Result<ProofData> = withContext(Dispatchers.IO) {
        try {
            val request = ProofRequest(challenge, claimType)
            val response = RetrofitClient.instance.generateProof(request)

            if (response.isSuccessful && response.body()?.status == "success") {
                val proofData = response.body()?.proof
                if (proofData != null) {
                    return@withContext Result.success(proofData)
                }
            }
            return@withContext Result.failure(Exception("Proof generation failed"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
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
package com.sentinel.wallet.models.network

import com.sentinel.wallet.models.network.*
import retrofit2.Response
import retrofit2.http.*

interface SentinelApiService {

    @GET("/authority/public_key")
    suspend fun getPublicKey(): Response<PublicKeyResponse>

    @POST("/demo/setup")
    suspend fun setupCredential(
        @Body request: SetupRequest
    ): Response<CredentialResponse>

    @POST("/verifier/challenge")
    suspend fun createChallenge(): Response<ChallengeResponse>

    @POST("/demo/proof")
    suspend fun generateProof(
        @Body request: ProofRequest
    ): Response<ProofResponse>

    @POST("/verifier/verify")
    suspend fun verifyProof(
        @Body request: VerifyRequest
    ): Response<VerifyResponse>
}
package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName

data class ChallengeResponse(
    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("challenge")
    val challenge: String
)

data class VerifyRequest(
    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("proof")
    val proof: ProofData
)

data class VerifyResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String?
)
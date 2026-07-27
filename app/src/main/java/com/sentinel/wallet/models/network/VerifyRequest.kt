package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName


data class VerifyRequest(

    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("proof")
    val proof: ProofData
)
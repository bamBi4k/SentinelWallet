package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName

data class ProofRequest(
    @SerializedName("challenge")
    val challenge: String,

    @SerializedName("claim_type")
    val claimType: String
)

data class ProofResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("proof")
    val proof: ProofData?
)

data class ProofData(
    @SerializedName("claim_type")
    val claimType: String,

    @SerializedName("value")
    val value: Boolean,

    @SerializedName("challenge")
    val challenge: String,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("signature")
    val signature: String,

    @SerializedName("public_key")
    val publicKey: String
)
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

data class SetupRequest(
    @SerializedName("birth_year")
    val birthYear: Int
)

data class PublicKeyResponse(
    @SerializedName("public_key")
    val publicKey: String
)
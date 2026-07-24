package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName

data class CredentialResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("credential")
    val credential: CredentialData?
)

data class CredentialData(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("birth_year")
    val birthYear: Int,

    @SerializedName("age_over_18")
    val ageOver18: Boolean,

    @SerializedName("issuer")
    val issuer: String,

    @SerializedName("issued_at")
    val issuedAt: String,

    @SerializedName("signature")
    val signature: String,

    @SerializedName("public_key")
    val publicKey: String
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
package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName


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
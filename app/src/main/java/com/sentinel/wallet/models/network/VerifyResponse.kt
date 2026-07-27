package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName


data class VerifyResponse(

    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String?
)
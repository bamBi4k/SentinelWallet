package com.sentinel.wallet.models.network


import com.google.gson.annotations.SerializedName

data class PublicKeyResponse(

    @SerializedName("public_key")
    val publicKey: String

)
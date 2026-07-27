package com.sentinel.wallet.models.network

import com.google.gson.annotations.SerializedName

data class SetupRequest(

    @SerializedName("birth_year")
    val birthYear: Int

)
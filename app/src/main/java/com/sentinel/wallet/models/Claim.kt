package com.sentinel.wallet.models

/**
 * Represents a single claim/attribute about a user
 * Example: AGE_OVER_18, EU_CITIZEN, HUMAN_VERIFIED
 */
data class Claim(
    val id: String,
    val name: String,
    val description: String,
    val isVerified: Boolean = false,
    val icon: String = "✅"
) {
    companion object {
        fun ageOver18(verified: Boolean = false) = Claim(
            id = "AGE_OVER_18",
            name = "Age Over 18",
            description = "Verified to be 18 years or older",
            isVerified = verified,
            icon = "🔞"
        )

        fun euCitizen(verified: Boolean = false) = Claim(
            id = "EU_CITIZEN",
            name = "EU Citizen",
            description = "Verified EU citizenship",
            isVerified = verified,
            icon = "🇪🇺"
        )

        fun humanVerified(verified: Boolean = false) = Claim(
            id = "HUMAN_VERIFIED",
            name = "Human Verified",
            description = "Verified as a real human",
            isVerified = verified,
            icon = "🧑"
        )

        fun governmentVerified(verified: Boolean = false) = Claim(
            id = "GOVERNMENT_VERIFIED",
            name = "Government Verified",
            description = "Verified by government authority",
            isVerified = verified,
            icon = "🏛️"
        )
    }
}
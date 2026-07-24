package com.sentinel.wallet.models

import java.util.UUID

/**
 * Represents a complete credential issued by Sentinel Authority
 */
data class Credential(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val issuer: String = "Sentinel Authority",
    val issuedAt: String,
    val expiresAt: String? = null,
    val claims: List<Claim> = emptyList(),
    val signature: String? = null,
    val isVerified: Boolean = false
) {
    fun getVerifiedClaims(): List<Claim> = claims.filter { it.isVerified }

    fun getUnverifiedClaims(): List<Claim> = claims.filter { !it.isVerified }

    fun getClaimCount(): Int = claims.size

    fun getVerifiedClaimCount(): Int = getVerifiedClaims().size
}
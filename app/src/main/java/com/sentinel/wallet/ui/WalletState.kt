package com.sentinel.wallet.ui

import com.sentinel.wallet.models.Claim
import com.sentinel.wallet.models.Credential

/**
 * Represents the entire UI state of the wallet
 */
sealed class WalletState {
    object Loading : WalletState()
    object NoCredential : WalletState()

    data class CredentialLoaded(
        val credential: Credential,
        val isConnected: Boolean = false
    ) : WalletState()

    data class Error(
        val message: String
    ) : WalletState()
}

/**
 * UI state for the wallet screen
 */
data class WalletUiState(
    val walletState: WalletState = WalletState.Loading,
    val selectedClaim: Claim? = null,
    val isProofGenerationInProgress: Boolean = false,
    val proofResult: ProofResult? = null
)

sealed class ProofResult {
    object Success : ProofResult()
    data class Failure(val message: String) : ProofResult()
}
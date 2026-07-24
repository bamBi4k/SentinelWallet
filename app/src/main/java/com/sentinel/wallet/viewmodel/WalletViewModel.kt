package com.sentinel.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.wallet.models.Claim
import com.sentinel.wallet.models.Credential
import com.sentinel.wallet.ui.ProofResult
import com.sentinel.wallet.ui.WalletState
import com.sentinel.wallet.ui.WalletUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadCredential()
    }

    private fun loadCredential() {
        viewModelScope.launch {
            // Simulate loading from storage
            delay(1000)

            // TODO: Replace with actual storage loading
            val demoCredential = createDemoCredential()
            _uiState.update { state ->
                state.copy(
                    walletState = WalletState.CredentialLoaded(demoCredential)
                )
            }
        }
    }

    private fun createDemoCredential(): Credential {
        val claims = listOf(
            Claim.ageOver18(verified = true),
            Claim.euCitizen(verified = true),
            Claim.humanVerified(verified = true),
            Claim.governmentVerified(verified = true)
        )

        return Credential(
            userId = "demo_user_123",
            issuer = "Sentinel Authority",
            issuedAt = "2026-07-24T10:00:00Z",
            claims = claims,
            isVerified = true
        )
    }

    fun selectClaim(claim: Claim) {
        _uiState.update { state ->
            state.copy(selectedClaim = claim)
        }
    }

    fun generateProof() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isProofGenerationInProgress = true,
                    proofResult = null
                )
            }

            // Simulate proof generation
            delay(1500)

            // Simulate success
            _uiState.update { state ->
                state.copy(
                    isProofGenerationInProgress = false,
                    proofResult = ProofResult.Success
                )
            }

            // Reset proof result after 3 seconds
            delay(3000)
            _uiState.update { state ->
                state.copy(proofResult = null)
            }
        }
    }

    fun refresh() {
        _uiState.update { state ->
            state.copy(
                walletState = WalletState.Loading
            )
        }
        loadCredential()
    }

    fun resetState() {
        _uiState.update { state ->
            state.copy(
                selectedClaim = null,
                proofResult = null,
                isProofGenerationInProgress = false
            )
        }
    }
}
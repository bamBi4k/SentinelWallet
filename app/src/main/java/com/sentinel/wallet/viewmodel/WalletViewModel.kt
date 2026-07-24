package com.sentinel.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.wallet.models.Claim
import com.sentinel.wallet.models.Credential
import com.sentinel.wallet.models.network.ProofData
import com.sentinel.wallet.repository.CredentialRepository
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

    private val repository = CredentialRepository()

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadCredential()
    }

    fun loadCredential(birthYear: Int = 2000) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(walletState = WalletState.Loading)
            }

            val result = repository.requestCredential(birthYear)

            result.onSuccess { credential ->
                _uiState.update { state ->
                    state.copy(
                        walletState = WalletState.CredentialLoaded(credential)
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        walletState = WalletState.Error(error.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    fun refresh() {
        loadCredential()
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

            try {
                // 1. Challenge erstellen
                val challengeResult = repository.createChallenge()
                if (challengeResult.isFailure) {
                    throw challengeResult.exceptionOrNull() ?: Exception("Challenge creation failed")
                }

                val challengeResponse = challengeResult.getOrThrow()

                // 2. Proof generieren
                val proofResult = repository.generateProof(
                    challengeResponse.challenge,
                    "AGE_OVER_18"
                )

                if (proofResult.isFailure) {
                    throw proofResult.exceptionOrNull() ?: Exception("Proof generation failed")
                }

                val proofData = proofResult.getOrThrow()

                // 3. Proof verifizieren
                val verifyResult = repository.verifyProof(
                    challengeResponse.sessionId,
                    proofData
                )

                if (verifyResult.isSuccess && verifyResult.getOrThrow()) {
                    _uiState.update { state ->
                        state.copy(
                            isProofGenerationInProgress = false,
                            proofResult = ProofResult.Success
                        )
                    }
                } else {
                    throw Exception("Verification failed")
                }

                delay(3000)
                _uiState.update { state ->
                    state.copy(proofResult = null)
                }

            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isProofGenerationInProgress = false,
                        proofResult = ProofResult.Failure(e.message ?: "Unknown error")
                    )
                }

                delay(3000)
                _uiState.update { state ->
                    state.copy(proofResult = null)
                }
            }
        }
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
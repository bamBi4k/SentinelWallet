package com.sentinel.wallet.viewmodel

import android.content.Context
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

class WalletViewModel(private val context: Context) : ViewModel() {

    private val repository = CredentialRepository(context)

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
                val currentState = _uiState.value.walletState
                if (currentState !is WalletState.CredentialLoaded) {
                    throw Exception("No credential loaded")
                }

                val credential = currentState.credential

                val challengeResult = repository.createChallenge()
                if (challengeResult.isFailure) {
                    throw challengeResult.exceptionOrNull() ?: Exception("Challenge creation failed")
                }

                val challengeResponse = challengeResult.getOrThrow()

                val proofData = repository.generateLocalProof(
                    challenge = challengeResponse.challenge,
                    claimType = "AGE_OVER_18",
                    credential = credential
                )

                if (proofData == null) {
                    throw Exception("Local proof generation failed")
                }

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

    fun generateProofWithChallenge(sessionId: String, challenge: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isProofGenerationInProgress = true,
                    proofResult = null,
                    qrVerificationResult = null
                )
            }

            try {
                val currentState = _uiState.value.walletState
                if (currentState !is WalletState.CredentialLoaded) {
                    throw Exception("No credential loaded")
                }

                val credential = currentState.credential

                val proofData = repository.generateLocalProof(
                    challenge = challenge,
                    claimType = "AGE_OVER_18",
                    credential = credential
                )

                if (proofData == null) {
                    throw Exception("Local proof generation failed")
                }

                // ✅ RICHTIG: Verwende den Parameter sessionId (nicht challengeResponse!)
                val verifyResult = repository.verifyProof(
                    sessionId,
                    proofData
                )

                if (verifyResult.isSuccess && verifyResult.getOrThrow()) {
                    _uiState.update { state ->
                        state.copy(
                            isProofGenerationInProgress = false,
                            proofResult = ProofResult.Success,
                            qrVerificationResult = "✅ QR-Verifizierung erfolgreich!"
                        )
                    }
                    callback(true)
                } else {
                    throw Exception("Verification failed")
                }

                delay(5000)
                _uiState.update { state ->
                    state.copy(
                        proofResult = null,
                        qrVerificationResult = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isProofGenerationInProgress = false,
                        proofResult = ProofResult.Failure(e.message ?: "Unknown error"),
                        qrVerificationResult = "❌ QR-Verifizierung fehlgeschlagen: ${e.message}"
                    )
                }
                callback(false)

                delay(5000)
                _uiState.update { state ->
                    state.copy(
                        proofResult = null,
                        qrVerificationResult = null
                    )
                }
            }
        }
    }

    fun clearQrResult() {
        _uiState.update { state ->
            state.copy(qrVerificationResult = null)
        }
    }

    fun resetState() {
        _uiState.update { state ->
            state.copy(
                selectedClaim = null,
                proofResult = null,
                isProofGenerationInProgress = false,
                qrVerificationResult = null
            )
        }
    }
}
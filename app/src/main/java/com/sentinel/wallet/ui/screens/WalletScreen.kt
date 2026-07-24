package com.sentinel.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.sentinel.wallet.ui.ProofResult
import com.sentinel.wallet.ui.WalletState
import com.sentinel.wallet.ui.WalletUiState
import com.sentinel.wallet.ui.components.ClaimCard
import com.sentinel.wallet.ui.components.StatusBadge
import com.sentinel.wallet.viewmodel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🛡️ Sentinel Wallet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.generateProof() },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Present Proof",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState.walletState) {
                is WalletState.Loading -> {
                    LoadingState()
                }

                is WalletState.NoCredential -> {
                    NoCredentialState()
                }

                is WalletState.CredentialLoaded -> {
                    CredentialLoadedState(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }

                is WalletState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading wallet...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NoCredentialState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔑",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Credential Found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Request a credential from Sentinel Authority",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Request credential */ }) {
                Text("Request Credential")
            }
        }
    }
}

@Composable
fun CredentialLoadedState(
    uiState: WalletUiState,
    viewModel: WalletViewModel
) {
    val credentialState = uiState.walletState as WalletState.CredentialLoaded
    val credential = credentialState.credential

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatusSection(
                isVerified = credential.isVerified,
                claimCount = credential.getClaimCount(),
                verifiedCount = credential.getVerifiedClaimCount()
            )
        }

        item {
            CredentialInfoCard(credential = credential)
        }

        item {
            Text(
                text = "Claims",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(credential.claims) { claim ->
            ClaimCard(
                claim = claim,
                onClick = { viewModel.selectClaim(claim) },
                isSelected = uiState.selectedClaim == claim
            )
        }

        if (uiState.proofResult != null) {
            item {
                ProofResultCard(
                    result = uiState.proofResult,
                    isInProgress = uiState.isProofGenerationInProgress
                )
            }
        }
    }
}

@Composable
fun StatusSection(
    isVerified: Boolean,
    claimCount: Int,
    verifiedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isVerified) {
                Color(0xFFE8F5E9)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isVerified) "🟢 Verified" else "🟡 Pending",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isVerified) Color(0xFF2E7D32) else Color(0xFFF57F17)
                )
                Text(
                    text = "$verifiedCount / $claimCount claims verified",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(isVerified = isVerified)
        }
    }
}

@Composable
fun CredentialInfoCard(credential: com.sentinel.wallet.models.Credential) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📄 Credential Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Issuer",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = credential.issuer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Issued At",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = credential.issuedAt.take(10),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ProofResultCard(
    result: ProofResult?,
    isInProgress: Boolean
) {
    if (result == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (result) {
                is ProofResult.Success -> Color(0xFFE8F5E9)
                is ProofResult.Failure -> Color(0xFFFFEBEE)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (result) {
                is ProofResult.Success -> {
                    Text(
                        text = "✅ Proof Generated Successfully!",
                        fontSize = 16.sp,
                        color = Color(0xFF2E7D32)
                    )
                }

                is ProofResult.Failure -> {
                    Text(
                        text = "❌ ${result.message}",
                        fontSize = 16.sp,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❌",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error Loading Wallet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
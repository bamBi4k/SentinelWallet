package com.sentinel.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sentinel.wallet.ui.components.QrScannerButton
import com.sentinel.wallet.viewmodel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: WalletViewModel = viewModel(),
    onProofResult: (Boolean) -> Unit = {}
) {
    var scanResult by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📷 QR-Code Scanner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onProofResult(false)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🛡️ QR-Code Verifizierung",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Scanne den QR-Code von der Sentinel Website",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            QrScannerButton(
                onScanResult = { result ->
                    scanResult = result
                    isVerifying = true
                    verificationResult = null

                    // Extrahiere session_id und challenge aus dem QR-Code
                    val params = result.substringAfter("?").split("&")
                    var sessionId = ""
                    var challenge = ""

                    params.forEach { param ->
                        when {
                            param.startsWith("session_id=") -> {
                                sessionId = param.substringAfter("=")
                            }
                            param.startsWith("challenge=") -> {
                                challenge = param.substringAfter("=")
                            }
                        }
                    }

                    if (sessionId.isNotEmpty() && challenge.isNotEmpty()) {
                        viewModel.generateProofWithChallenge(sessionId, challenge) { success ->
                            isVerifying = false
                            if (success) {
                                verificationResult = "✅ Verifizierung erfolgreich!"
                                onProofResult(true)
                            } else {
                                verificationResult = "❌ Verifizierung fehlgeschlagen"
                                onProofResult(false)
                            }
                        }
                    } else {
                        isVerifying = false
                        verificationResult = "❌ Ungültiger QR-Code"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            if (isVerifying) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
                Text(
                    text = "Verifiziere...",
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (verificationResult != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (verificationResult!!.contains("✅")) {
                            Color(0xFFE8F5E9)
                        } else {
                            Color(0xFFFFEBEE)
                        }
                    )
                ) {
                    Text(
                        text = verificationResult!!,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (verificationResult!!.contains("✅")) {
                            Color(0xFF2E7D32)
                        } else {
                            Color(0xFFC62828)
                        }
                    )
                }
            }

            if (scanResult != null && verificationResult == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "📋 QR-Code erkannt!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = scanResult!!.take(50) + "...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
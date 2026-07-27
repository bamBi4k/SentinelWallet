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
import com.sentinel.wallet.ui.components.QrScannerButton
import com.sentinel.wallet.viewmodel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit = {}
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
                            onBack()
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
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Scanne den QR-Code der Sentinel Website",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Spacer(modifier = Modifier.height(32.dp))


            QrScannerButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                onScanResult = { result ->

                    scanResult = result
                    isVerifying = true
                    verificationResult = null


                    val uri = android.net.Uri.parse(result)

                    val sessionId =
                        uri.getQueryParameter("session_id")

                    val challenge =
                        uri.getQueryParameter("challenge")


                    if (
                        sessionId != null &&
                        challenge != null
                    ) {

                        viewModel.generateProofWithChallenge(
                            sessionId = sessionId,
                            challenge = challenge
                        )

                        isVerifying = false

                        verificationResult =
                            "✅ Proof wird gesendet..."


                    } else {

                        isVerifying = false

                        verificationResult =
                            "❌ Ungültiger Sentinel QR-Code"
                    }
                }
            )


            if (isVerifying) {

                Spacer(modifier = Modifier.height(24.dp))

                CircularProgressIndicator()

                Text(
                    text = "Verifiziere Proof...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }


            verificationResult?.let { result ->

                Spacer(modifier = Modifier.height(24.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (result.contains("✅"))
                                Color(0xFFE8F5E9)
                            else
                                Color(0xFFFFEBEE)
                    )

                ) {

                    Text(
                        text = result,

                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),

                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            scanResult?.let {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "QR erkannt:",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = it.take(80),
                    fontSize = 12.sp
                )
            }
        }
    }
}
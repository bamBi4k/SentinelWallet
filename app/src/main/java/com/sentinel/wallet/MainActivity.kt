package com.sentinel.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sentinel.wallet.ui.screens.QrScannerScreen
import com.sentinel.wallet.ui.screens.WalletScreen
import com.sentinel.wallet.ui.theme.SentinelWalletTheme
import com.sentinel.wallet.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SentinelWalletTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Navigation zwischen Wallet und Scanner
                    var currentScreen by remember { mutableStateOf("wallet") }

                    when (currentScreen) {
                        "wallet" -> {
                            WalletScreen(
                                viewModel = WalletViewModel(this),
                                onOpenScanner = {
                                    currentScreen = "scanner"
                                }
                            )
                        }
                        "scanner" -> {
                            QrScannerScreen(
                                viewModel = WalletViewModel(this),
                                onProofResult = { success ->
                                    // Nach Verifizierung zurück zur Wallet
                                    currentScreen = "wallet"
                                }
                                // onBack wurde entfernt!
                            )
                        }
                    }
                }
            }
        }
    }
}
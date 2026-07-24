package com.sentinel.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
                    // Context wird hier übergeben
                    WalletScreen(
                        viewModel = WalletViewModel(this)
                    )
                }
            }
        }
    }
}
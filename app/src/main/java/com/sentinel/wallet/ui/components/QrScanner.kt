package com.sentinel.wallet.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QrScannerButton(
    onScanResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Scanner Launcher
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            onScanResult(result.contents)
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted → start scanner
            startScanner(scanLauncher)
        }
    }

    Button(
        onClick = {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted → start scanner
                    startScanner(scanLauncher)
                }
                else -> {
                    // Request permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A6CF7)
        )
    ) {
        Text(
            text = "📷 QR-Code scannen",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// Hilfsfunktion zum Starten des Scanners
private fun startScanner(scanLauncher: androidx.activity.result.ActivityResultLauncher<ScanOptions>) {
    val options = ScanOptions()
    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
    options.setPrompt("Scanne den QR-Code der Sentinel Website")
    options.setCameraId(0)
    options.setBeepEnabled(false)
    options.setBarcodeImageEnabled(true)
    options.setOrientationLocked(false)
    scanLauncher.launch(options)
}
package com.sentinel.wallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(
    isVerified: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isVerified) "✓ Verified" else "Pending",
        modifier = modifier
            .background(
                color = if (isVerified) Color(0xFF4CAF50) else Color(0xFFFFA726),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}
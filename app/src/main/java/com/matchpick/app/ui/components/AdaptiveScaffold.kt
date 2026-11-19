package com.matchpick.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveMatchLayout(
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 600.dp

        if (isExpanded) {
            // Tablet / Katlanabilir Cihaz (Geniş Ekran): Çift Panel Görünümü
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1.2f)) {
                    primaryContent()
                }
                Box(modifier = Modifier.weight(0.8f)) {
                    secondaryContent()
                }
            }
        } else {
            // Standart Mobil Ekran: Tekil Dikey Akış
            Box(modifier = Modifier.fillMaxSize()) {
                primaryContent()
            }
        }
    }
}

package com.matchpick.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun AdaptiveMatchLayout(
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
        // Tablet / Katlanabilir Cihaz (Geniş Ekran): Çift Panel Görünümü
        Row(
            modifier = modifier.fillMaxSize(),
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
        Box(modifier = modifier.fillMaxSize()) {
            primaryContent()
        }
    }
}

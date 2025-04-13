package com.doubletapp_hw.screens.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.doubletapp_hw.screens.NavDrawer
import com.doubletapp_hw.screens.Routes

@Composable
fun InfoScreen() {
    NavDrawer(Routes.Info) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Привычки от Светика",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "version-hw08.0",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
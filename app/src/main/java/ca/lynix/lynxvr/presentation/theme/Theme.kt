package ca.lynix.lynxvr.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun LynxVRTheme(
        content: @Composable () -> Unit
) {
    MaterialTheme(
            colors = lynxvrTheme,
            content = content,
            typography = lynxvrTypography
    )
}
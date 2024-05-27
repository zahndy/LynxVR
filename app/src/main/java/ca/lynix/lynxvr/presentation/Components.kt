package ca.lynix.lynxvr.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import ca.lynix.lynxvr.presentation.components.InputBox

@Composable
fun ToggleButtonChip(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    isServiceRunning: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        Chip(
            modifier = modifier.padding(15.dp),
            onClick = onClick,
            label = {
                Text(
                    text = if (isServiceRunning) "Stop" else "Start",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            icon = {
                Icon(
                    imageVector = if (isServiceRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "toggle heart rate service",
                    modifier = iconModifier
                )
            },
        )
    }
}
@Composable
fun ToggleOSCChip(
    modifier: Modifier = Modifier,
    isOSC: Boolean,
    onCheckedChange : () -> Unit,
) {
    var checked by remember { mutableStateOf(false) }

    ToggleChip(
        modifier = modifier,
        checked = isOSC,
        toggleControl = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = isOSC,
                    modifier = Modifier.semantics {
                        this.contentDescription = if (checked) "On" else "Off"
                    }
                )
            }
        },
        onCheckedChange = {
            checked = isOSC
            onCheckedChange()
        },
        label = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                    // spacing between icon and text

                ) {
                    Icon(
                        imageVector = Icons.Rounded.Wifi, // Change to your desired icon
                        contentDescription = null // Decorative element
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Use OSC",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
    )
}

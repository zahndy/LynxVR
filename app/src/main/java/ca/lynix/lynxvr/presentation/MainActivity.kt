/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package ca.lynix.lynxvr.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import ca.lynix.lynxvr.presentation.components.InputBox
import ca.lynix.lynxvr.presentation.theme.LynxVRTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.Config

class MainActivity : ComponentActivity() {

    object Config {
        const val CONF_HTTP_HOSTNAME = "HTTP_HOSTNAME"
        const val CONF_HTTP_PORT = "HTTP_PORT"
        const val CONF_NEOS_WS_PORT = "NEOS_PORT"

        const val CONF_HTTP_HOSTNAME_DEFAULT = "127.0.0.1"
        const val CONF_HTTP_PORT_DEFAULT = 9000
        const val CONF_NEOS_WS_PORT_DEFAULT = 9555

        const val CONF_BROADCAST_HEARTRATE_UPDATE = "lynxhr.updateHeartRate"
        const val CONF_BROADCAST_STATUS = "lynxhr.updateStatus"

        const val CONF_SENDING_STATUS_OK = "ok"
        const val CONF_SENDING_STATUS_ERROR = "error"
        const val CONF_SENDING_STATUS_NOT_RUNNING = "not_running"
        const val CONF_SENDING_STATUS_STARTING = "starting"
    }

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        initConfig()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android",preferences)
        }
    }

    private fun initConfig() {
        preferences = this.getSharedPreferences(packageName + "_preferences", MODE_PRIVATE)

        with(preferences.edit()) {
            if (!preferences.contains(Config.CONF_HTTP_HOSTNAME)) {
                putString(Config.CONF_HTTP_HOSTNAME, Config.CONF_HTTP_HOSTNAME_DEFAULT)
            }

            if (!preferences.contains(Config.CONF_HTTP_PORT)) {
                putInt(Config.CONF_HTTP_PORT, Config.CONF_HTTP_PORT_DEFAULT)
            }

            if (!preferences.contains(Config.CONF_NEOS_WS_PORT)) {
                putInt(Config.CONF_NEOS_WS_PORT, Config.CONF_NEOS_WS_PORT_DEFAULT)
            }
            apply()
        }
    }

}



@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearApp(greetingName: String, preferences: SharedPreferences ) {

    var isServiceRunning by remember { mutableStateOf(false) }
    var isOSC by remember { mutableStateOf(false) }

    LynxVRTheme {
        AppScaffold {
            var listState = rememberResponsiveColumnState(
                contentPadding = ScalingLazyColumnDefaults.padding(
                    first = ItemType.SingleButton,
                    last = ItemType.Chip,
                ),
            )
            val contentModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            val iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)

            ScreenScaffold (
                scrollState = listState
            ) {
                ScalingLazyColumn(columnState = listState) {
                    /* Add Text field for heartrate and battery here */
                    if (isServiceRunning) {
                        item {
                            // Heart icon with primary color from theme
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Favorite, // Change to your desired icon
                                    contentDescription = null // Decorative element
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "0", // Replace 100 with the actual heart rate value
                                    color = Color.White, // Use white color for the text
                                )
                            }
                        }
                    }
                    item { ToggleButtonChip(contentModifier, isServiceRunning = isServiceRunning, onClick = { isServiceRunning = !isServiceRunning }) }
                    item { ToggleOSCChip(contentModifier, isOSC = isOSC, onCheckedChange = { isOSC = !isOSC }) }
                    if (!isOSC) {
                    item {
                        InputBox(
                            label = "Server Address",
                            placeholder = "",
                            value = "api.lynix.ca",
                            onChange = { value -> with(preferences.edit()) {
                                putString(MainActivity.Config.CONF_HTTP_HOSTNAME, value) // figure out later
                                apply()
                            }


                            }
                        )
                    }
                        item {
                                InputBox(
                                    label = "Server Port",
                                    placeholder = "",
                                    value = "",
                                    onChange = { value ->
                                        with(preferences.edit()) {
                                            putInt(
                                                MainActivity.Config.CONF_HTTP_PORT,
                                                value.toInt()
                                            )
                                            apply()
                                        }
                                    }
                                )
                            }
                        } else {
                        item {
                            InputBox(
                                label = "Target Address",
                                placeholder = "0.0.0.0",
                                value = "",
                                onChange = { value -> with(preferences.edit()) {
                                    putString(MainActivity.Config.CONF_HTTP_HOSTNAME, value)
                                    apply()
                                }
                                }
                            )
                        }
                    }
                    item{
                        Text(
                            text = "LynxVR v2.0.0", // Replace 100 with the actual heart rate value
                            color = Color.White, // Use white color for the text
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    }
                }
            }
        }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    lateinit var preferences: SharedPreferences
    WearApp("Preview Android", preferences)
}
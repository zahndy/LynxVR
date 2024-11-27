package ca.lynix.lynxvr.presentation.services
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LifecycleService
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.gustavoavila.websocketclient.WebSocketClient
import android.support.v4.content.LocalBroadcastManager
import androidx.core.content.ContextCompat
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import ca.lynix.lynxvr.presentation.MainActivity
import kotlin.math.roundToInt


class LynxVrService: LifecycleService(), SensorEventListener {
    // The backend needs to be written a little bit due to the new UI functionality and design.
    //private Context cntx
    private lateinit var mHeartRateSensor: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var httpQueue: RequestQueue
    private lateinit var preferences: SharedPreferences

    //Resonite Websocket Server
    private var webSocketClient: WebSocketClient? = null



    private fun createWebSocketClient() {
        val uri: URI
        try {
            uri = URI("ws://" + preferences.getString(
                MainActivity.Config.CONF_HTTP_HOSTNAME,
                MainActivity.Config.CONF_HTTP_HOSTNAME_DEFAULT
            ).toString() + ":" + preferences.getInt(
                MainActivity.Config.CONF_NEOS_WS_PORT,
                MainActivity.Config.CONF_NEOS_WS_PORT_DEFAULT
            ).toString())
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen() {
                println("onOpen")
                webSocketClient!!.send("0")
            }

            override fun onTextReceived(message: String) {
                println("onTextReceived")
            }

            override fun onBinaryReceived(data: ByteArray) {
                println("onBinaryReceived")
            }

            override fun onPingReceived(data: ByteArray) {
                println("onPingReceived")
            }

            override fun onPongReceived(data: ByteArray) {
                println("onPongReceived")
            }

            override fun onException(e: java.lang.Exception) {
                println(e.message)
            }

            override fun onCloseReceived(p0: Int, p1: String?) {
                println("onCloseReceived")
            }
        }
        (webSocketClient as WebSocketClient).setConnectTimeout(10000)
        (webSocketClient as WebSocketClient).setReadTimeout(60000)
        (webSocketClient as WebSocketClient).enableAutomaticReconnection(5000)
        (webSocketClient as WebSocketClient).connect()
    }

    private fun startMeasure() {

        val sensorRegistered: Boolean = mSensorManager.registerListener(
            this,
            mHeartRateSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        Log.d("Sensor Status:", " Sensor registered: " + (if (sensorRegistered) "yes" else "no"))
        sendStatusToActivity(MainActivity.Config.CONF_SENDING_STATUS_STARTING)
    }

    private fun stopMeasure() {
        mSensorManager.unregisterListener(this)
        sendStatusToActivity(MainActivity.Config.CONF_SENDING_STATUS_NOT_RUNNING)
    }

    private fun sendHeartRate(heartrate: Int) {

        val gfgThread = Thread {
            try {
                try {
                   /* var osc = OSCPortOut(
                        InetAddress.getByName( preferences.getString(
                            MainActivity.Config.CONF_HTTP_HOSTNAME,
                            MainActivity.Config.CONF_HTTP_HOSTNAME_DEFAULT
                        )), preferences.getInt(
                            MainActivity.Config.CONF_HTTP_PORT,
                            MainActivity.Config.CONF_HTTP_PORT_DEFAULT
                        )
                    )
                    osc.send(OSCMessage(DEFAULT_VRC_ENDPOINT_HR1, listOf(heartrate)))
                    osc.send(OSCMessage(DEFAULT_VRC_ENDPOINT_HR2, listOf((heartrate.toFloat()/255f))))
                    osc.send(OSCMessage(DEFAULT_VRC_ENDPOINT_HR3, listOf((heartrate.toFloat()/127f-1f))))
                    osc.send(OSCMessage(DEFAULT_VRC_ENDPOINT_BAT, listOf(0)))
                    osc.send(OSCMessage(DEFAULT_VRC_ENDPOINT_BAT_CHAR, listOf(false)))
                    */
                    // Battery Monitoring
                    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                        this.registerReceiver(null, ifilter)
                    }

                    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                    val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                            || status == BatteryManager.BATTERY_STATUS_FULL

                    val batteryPct: Float? = batteryStatus?.let { intent ->
                        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        level * 100 / scale.toFloat()
                    }

                    // Resonite Client
                    webSocketClient!!.send("bpm=$heartrate,bat=$batteryPct,bat_charging=$isCharging");
                    sendStatusToActivity(MainActivity.Config.CONF_SENDING_STATUS_OK)
                } catch(e: Exception) {
                    Log.e("OSC", e.toString())
                    sendStatusToActivity(MainActivity.Config.CONF_SENDING_STATUS_ERROR)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        gfgThread.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val mHeartRateFloat: Float = event!!.values[0]

        val mHeartRate: Int = mHeartRateFloat.roundToInt()
        Log.d("HR: ", mHeartRate.toString())

        sendHeartRate(mHeartRate)
        sendHeartRateToActivity(mHeartRate)
    }

    override fun onDestroy() {
        stopMeasure()
        // Stop NeosVR Server

        // Stop NeosVR Client
        webSocketClient!!.close(0,1005,"closed");
        super.onDestroy()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // ignored
    }


    private fun sendHeartRateToActivity(heartrate: Int) {
            val intent = Intent(MainActivity.Config.CONF_BROADCAST_HEARTRATE_UPDATE)
            intent.putExtra("heartrate", heartrate)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }

        private fun sendStatusToActivity(status: String) {
            val intent = Intent(MainActivity.Config.CONF_BROADCAST_STATUS)
            intent.putExtra("status", status)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }




}
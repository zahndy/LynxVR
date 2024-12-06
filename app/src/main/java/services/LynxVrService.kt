package ca.lynix.lynxvr.presentation.services
//import android.support.v4.content.LocalBroadcastManager
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.lynix.lynxvr.R
import ca.lynix.lynxvr.presentation.MainActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.gustavoavila.websocketclient.WebSocketClient
import java.net.URI
import java.net.URISyntaxException
import java.util.UUID
import kotlin.math.roundToInt


class LynxVrService(liveData: MutableLiveData<String>): LifecycleService(), SensorEventListener {
    // The backend needs to be written a little bit due to the new UI functionality and design.

    private lateinit var mHeartRateSensor: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var httpQueue: RequestQueue
    private lateinit var preferences: SharedPreferences

    private val CHANNEL_ID = "HeartRateService"
    //Resonite Websocket Server
    private var webSocketClient: WebSocketClient? = null
    val tokenLiveData = liveData

    companion object {
        fun startService(context: Context,) {
            Log.d("service", "Starting service ...")
            val startIntent = Intent(context, LynxVrService::class.java)
           // ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            Log.d("service", "Stopping service ...")
            val stopIntent = Intent(context, LynxVrService::class.java)
           // context.stopService(stopIntent)
        }
    }
    //fun LynxVrService(){}
    //constructor() : this(null){}

    private fun createWebSocketClient() {
        Log.d("service", "Create WebSocket Client ...")
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

    var mainIntent:Intent? = null;

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mainIntent=intent;
        doSomething()

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(R.string.notification_title.toString())
            .setContentText(R.string.notification_text.toString())
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        // Start Neos Server

        // Create Neos Websocket Client
        createWebSocketClient();

        return START_NOT_STICKY

    }


    @SuppressLint("MissingSuperCall")
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun startNeosServer() {
        // Neos Websocket Server
        //wsServer.start()

        //Log.d("NEOSVR WS", "The NeosVR Websocket Server has been started.")

    }

    private fun stopNeosServer() {
        // Neos Websocket Server
        //wsServer.stop()
        //Log.d("NEOSVR WS", "The NeosVR Websocket Server has been stopped.")
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            R.string.notification_channel_title.toString(),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }

    private fun doSomething() {

        preferences = this.getSharedPreferences(packageName + "_preferences", MODE_PRIVATE)
        httpQueue = Volley.newRequestQueue(this)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!

        val sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        val arrayList = ArrayList<String>()
        for (sensor in sensors) {
            arrayList.add(sensor.name)
        }
        arrayList.forEach { n -> System.out.println("LynxSensor: " + n) }

        val thread1: Thread = object : Thread() {
            override fun run() {
                //You can remove this loop and replace it with your logic
                val token1 = UUID.randomUUID().toString()
                startMeasure()
                //Handler(Looper.getMainLooper()).post { sendTokenToObserver("Thread1: $token1") }

            }
        }
        thread1.start()


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
        Log.d("sendHeartRate", "heartrate$Int")
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

    fun sendTokenToObserver(token: String?) {
        tokenLiveData.value = token!!
    }

    private fun sendHeartRateToActivity(heartrate: Int) {
            val intent = Intent(MainActivity.Config.CONF_BROADCAST_HEARTRATE_UPDATE)
            intent.putExtra("heartrate", heartrate)
            //LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            Handler(Looper.getMainLooper()).post { sendTokenToObserver("Thread1: $intent")}
        //.
    }

    private fun sendStatusToActivity(status: String) {
        val intent = Intent(MainActivity.Config.CONF_BROADCAST_STATUS)
        intent.putExtra("status", status)
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
      //Handler(Looper.getMainLooper()).post { sendTokenToObserver("Thread2: $intent")}
    }


}
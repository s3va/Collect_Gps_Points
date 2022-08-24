package tk.kvakva.collectgpspoints

import android.Manifest
import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


private const val TAG = "GeoPointCollectServ"
const val STOP_GEO_SERVICE = "Stop Geo Service"
private const val NOTI_TITLE = "Notification title"
private const val NOTI_TXT = "Notification message."
private const val CHANNEL_STR_ID = "ChannelSTRid001"
private const val ONGOING_NOTIFICATION_ID = 666

class GeoPointCollectServ : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val geoDb by lazy {
        GeoPointRepository(GeoPointRoomDatabase.getDatabase(this.applicationContext).geoPointDao())
        //GeoPointRoomDatabase.getDatabase(applicationContext)
    }

    //private val geoPointDao: GeoPointDao by lazy {
    //    geoDb.geoPointDao()
    //}
    private val locationManager: LocationManager by lazy {
        getSystemService(LOCATION_SERVICE) as LocationManager
    }

    private val locationListener = LocationListener {
        Log.e(TAG, "location: latitude=${it.latitude} longitude=${Instant.ofEpochMilli(it.time)}")

        it.accuracy = 1.0f

        Log.e(
            TAG, "location: ${it.time} accuracy=${it.accuracy} speed=${
                it.speed
            } speedAccuracy=${
                if
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    it.speedAccuracyMetersPerSecond
                else
                    -1111.1111
            }"
        )
        Log.e(TAG, "bearing=${it.bearing} ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "bearingAccuracyDegrees=${it.bearingAccuracyDegrees} ")
        }
        Log.e(TAG, "provider=${it.provider} ")
        it.dump({ dumpit: String ->
            Log.e(TAG, dumpit)
        }, "geo print: ")
        scope.launch {
            geoDb.insertAll(
                GeoPoint(
                    id = 0,
                    smartDateTime = LocalDateTime.now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern(DATETIME_FORMAT)),
                    lat = it.latitude,
                    lon = it.longitude,
                    gpsDateTime = LocalDateTime
                        .ofInstant(Instant.ofEpochMilli(it.time), ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern(DATETIME_FORMAT)),
                    accuracy = it.accuracy,
                    speed =
                    if (it.hasSpeed())
                        it.speed
                    else
                        null,
                    speedAccuracy =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        && it.hasSpeedAccuracy()
                    )
                        it.speedAccuracyMetersPerSecond
                    else
                        null,
                    provider = it.provider,

                    )
            )
            val widgetManager = AppWidgetManager.getInstance(applicationContext)
            val remoteViews = RemoteViews(
                applicationContext.packageName,
                R.layout.geo_points_collect_app_widget
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setTextViewText(
                        R.id.appwidget_text,
                        """latitude: ${it.latitude}
                           |longitude: ${it.longitude}
                           |time : ${
                            LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(it.time), ZoneOffset.UTC)
                                .format(DateTimeFormatter.ofPattern(DATETIME_FORMAT))
                           } accuracy: ${it.accuracy}
                           |speed: ${it.speed}
                           |speedAccuracy: ${it.speedAccuracyMetersPerSecond}
                           |provider: ${it.provider}""".trimMargin()
                    )
                } else {
                    setTextViewText(
                        R.id.appwidget_text,
                        """latitude: ${it.latitude}
                           |longitude: ${it.longitude}
                           |time : ${
                            LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(it.time), ZoneOffset.UTC)
                                .format(DateTimeFormatter.ofPattern(DATETIME_FORMAT))
                           } accuracy: ${it.accuracy}
                           |speed: ${it.speed}
                           |pr:${it.provider}""".trimMargin()
                    )
                }
            }
//            widgetManager.updateAppWidget(
//                ComponentName(
//                    packageName,
//                    GeoPointsCollectAppWidget::class.java.name
//                ), remoteViews
//            )
            val widgetsIds: IntArray = widgetManager.getAppWidgetIds(
                ComponentName(
                    packageName,
                    GeoPointsCollectAppWidget::class.java.name
                )
            )
            widgetsIds.forEach { widgetId: Int ->
                widgetManager.partiallyUpdateAppWidget(widgetId, remoteViews)
            }
        }
    }

    //private val timer = Timer()
    //private val executorService1: ScheduledExecutorService =
    //  Executors.newSingleThreadScheduledExecutor()
    //private val handler = Handler(Looper.getMainLooper())

//    private val runnable: Runnable = object : Runnable {
//        /**
//         * When an object implementing interface `Runnable` is used
//         * to create a thread, starting the thread causes the object's
//         * `run` method to be called in that separately executing
//         * thread.
//         *
//         *
//         * The general contract of the method `run` is that it may
//         * take any action whatsoever.
//         *
//         * @see java.lang.Thread.run
//         */
//        override fun run() {
//            //Thread {
//            Log.e(TAG, "-- run in thread: sleep-----")
//            Log.e(TAG, "!! before delay -----")
//            Thread.sleep(2000)
//            Log.e(TAG, "-- run in thread: runnable--")
//
//            if (InetAddress.getByName("192.168.12.1").isReachable(2000))
//                Log.e(TAG, "run: 192.168.12.1 IS REACHABLE")
//            else
//                Log.e(TAG, "run: 192.168.12.1 is not reachable :( :( :(")
//
//            Log.i(TAG, "----------------------------------------------------\n")
//            //}.start()
//
//            //executorService1.schedule(this, toNextTenthSecond(), TimeUnit.MILLISECONDS)
//            //handler.postDelayed(this, toNextTenthSecond())
//        }
//    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
        var geoPointCollectServ = MutableLiveData<GeoPointCollectServ?>(null)
    }

    override fun onCreate() {
        super.onCreate()
        geoPointCollectServ.value = this
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener, Looper.getMainLooper())
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            60000,
            20f,
            locationListener
        )

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            60000,
            20f,
            locationListener
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationManager.requestLocationUpdates(
                LocationManager.FUSED_PROVIDER,
                60000,
                20f,
                locationListener
            )
        }

        //locationManager.getCurrentLocation()

        changeWidget(applicationContext, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
        geoPointCollectServ.value = null
        locationManager.removeUpdates(locationListener)
        //handler.removeCallbacks(runnable)
        //executorService1.shutdownNow()
        //timer.cancel()
        //timer.purge()

        changeWidget(applicationContext, false)

    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * [android.content.Context.startService], providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     *
     *
     * For backwards compatibility, the default implementation calls
     * [.onStart] and returns either [.START_STICKY]
     * or [.START_STICKY_COMPATIBILITY].
     *
     *
     * Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use [android.os.AsyncTask].
     *
     * @param intent The Intent supplied to [android.content.Context.startService],
     * as given.  This may be null if the service is being restarted after
     * its process has gone away, and it had previously returned anything
     * except [.START_STICKY_COMPATIBILITY].
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with [.stopSelfResult].
     *
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the [.START_CONTINUATION_MASK] bits.
     *
     * @see .stopSelfResult
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.action?.let {
            if (it == STOP_GEO_SERVICE) {
                stopSelf()
                return START_NOT_STICKY
            }
        }
        //countDownTimer.start()
        //handler.postDelayed(runnable, toNextTenthSecond())
        //executorService1.schedule(runnable, toNextTenthSecond(),TimeUnit.MILLISECONDS)
        //executorService1.scheduleWithFixedDelay(runnable, toNextTenthSecond(),10000,TimeUnit.MILLISECONDS)
        //executorService1.scheduleAtFixedRate(
        //    runnable,
        //    toNextTenthSecond(),
        //    10000,
        //    TimeUnit.MILLISECONDS
        //)
//        val timerTask = object : TimerTask() {
//            /**
//             * The action to be performed by this timer task.
//             */
//            override fun run() {
//                runnable.run()
//                if (InetAddress.getByName("seva.kvakva.tk").isReachable(2000))
//                    Log.e(TAG, "run: seva.kvakva.tk IS REACHABLE")
//                else
//                    Log.e(TAG, "run: seva.kvakva.tk is not reachable :( :( :(")
//            }
//        }
        //timer.scheduleAtFixedRate(timerTask, toNextTenthSecond(),10000)


        val mNotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // mNotificationManager?.createNotificationChannelGroup( NotificationChannelGroup("chats_group", "Chats") )
            val notificationChannel =
                NotificationChannel(
                    CHANNEL_STR_ID, "Service Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            //notificationChannel.enableLights(false)
            //notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            mNotificationManager?.createNotificationChannel(notificationChannel)

            val pendingIntent: PendingIntent? =
//                Intent(this, MainActivity::class.java).let { notificationIntent ->
//                    notificationIntent.action=Intent.ACTION_MAIN
//                    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //packageManager.getLaunchIntentForPackage("tk.kvakva.ipcam005")
                packageManager.getLaunchIntentForPackage(baseContext.packageName)
                    //?.setPackage(null)
                    //?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    ?.let { notificationIntent ->
                        notificationIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        notificationIntent.`package` = null
                        notificationIntent.putExtra("service", 1)
                        PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }

            val notification: Notification =
                Notification.Builder(this, CHANNEL_STR_ID)
                    .setContentTitle(NOTI_TITLE)
                    .setContentText(NOTI_TXT)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()

// Notification ID cannot be 0.
            startForeground(ONGOING_NOTIFICATION_ID, notification)
        }
        return START_NOT_STICKY
    }
}

val countDownTimer = object : CountDownTimer(180000, 10000) {
    override fun onTick(p0: Long) {
        Log.e(TAG, "onTick: p0=$p0")
    }

    override fun onFinish() {
        Log.e(TAG, "onFinish: COUNT DOWN TIMER")
    }


}

fun changeWidget(context: Context, started: Boolean) {
    val widgetManager = AppWidgetManager.getInstance(context)
    val remoteViews = RemoteViews(
        context.packageName,
        R.layout.geo_points_collect_app_widget
    ).apply {
        setImageViewResource(
            R.id.imageButton,
            if (started)
                R.drawable.ic_baseline_stop_24
            else
                R.drawable.ic_baseline_not_started_24
        )

        setOnClickPendingIntent(
            R.id.imageButton,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    context,
                    5555,
                    Intent(context, GeoPointCollectServ::class.java).apply {
                        if (started)
                            action = STOP_GEO_SERVICE
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(
                    context,
                    5555,
                    Intent(context, GeoPointCollectServ::class.java).apply {
                        if (started)
                            action = STOP_GEO_SERVICE
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        )

    }
//            widgetManager.updateAppWidget(
//                ComponentName(
//                    packageName,
//                    GeoPointsCollectAppWidget::class.java.name
//                ), remoteViews
//            )
    val widgetsIds: IntArray = widgetManager.getAppWidgetIds(
        ComponentName(
            context.packageName,
            GeoPointsCollectAppWidget::class.java.name
        )
    )
    widgetsIds.forEach { widgetId: Int ->
        widgetManager.partiallyUpdateAppWidget(widgetId, remoteViews)
    }
}

//private fun toNextTenthSecond(): Long {
//    val localTime = LocalTime.now()
//    Log.e(TAG, "run: localTime=$localTime")
//    val netxtTime =
//        LocalTime.of(localTime.hour, localTime.minute, localTime.second / 10 * 10)
//            .plusSeconds(10)
//    Log.e(TAG, "run: netxtTime=$netxtTime")
//    val microUntilNext = localTime.until(netxtTime, ChronoUnit.MILLIS)
//    Log.e(TAG, "run: microUntilNext=$microUntilNext")
//    return microUntilNext
//}

package tk.kvakva.collectgpspoints

import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.number.NumberFormatter
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of App Widget functionality.
 */
class GeoPointsCollectAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val pendingResult: BroadcastReceiver.PendingResult = goAsync()
            updateAppWidget(context, appWidgetManager, appWidgetId, pendingResult)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

private const val TAG = "GeoPointsCollectAppWidg"
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    pendingResult: BroadcastReceiver.PendingResult
) {

    val cj = Job()
    val coroutineScopeApWi = CoroutineScope(Dispatchers.IO + cj)
    //val widgetText = context.getString(R.string.appwidget_text)
//    val geoPointRepository = GeoPointRepository(GeoPointRoomDatabase.getDatabase(context).geoPointDao())
//    val lp: GeoPoint? = geoPointRepository.allGeoPointEntries.asLiveData().value?.last()
    coroutineScopeApWi.launch {
        val lp: GeoPoint = GeoPointRoomDatabase.getDatabase(context).geoPointDao().getAll().last()
        val str =
            """latitude: ${lp.lat} longitude: ${lp.lon}
          |time : ${lp.gpsDateTime} accuracy: ${lp.accuracy}
          |speed: ${lp.speed} speedAccuracy: ${lp.speedAccuracy}""".trimMargin()
        Log.e(TAG, "updateAppWidget: $str")
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.geo_points_collect_app_widget)
        views.setTextViewText(R.id.appwidget_text, str)
        val servRunning: Boolean =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .getRunningServices(Integer.MAX_VALUE)
                .any { it.service.className == GeoPointCollectServ::class.java.name }

        views.setImageViewResource(
            R.id.imageButton,
            if (servRunning)
                R.drawable.ic_baseline_stop_24
            else
                R.drawable.ic_baseline_not_started_24
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            views.setOnClickPendingIntent(
                R.id.imageButton,
                PendingIntent.getForegroundService(
                    context,
                    333,
                    Intent(context, GeoPointCollectServ::class.java).apply {
                        if (servRunning)
                            action = STOP_GEO_SERVICE
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            views.setOnClickPendingIntent(
                R.id.imageButton,
                PendingIntent.getService(
                    context,
                    333,
                    Intent(context, GeoPointCollectServ::class.java).apply {
                        if (servRunning)
                            action = STOP_GEO_SERVICE
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
        pendingResult.finish()
    }

}

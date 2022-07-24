package com.example.roxiemobiletesttask.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.roxiemobiletesttask.R
import com.example.roxiemobiletesttask.staticpackage.Constants
import java.io.*
import java.util.*

class ClearCacheService : Service()
{
    companion object {
        @SuppressLint("UnspecifiedImmutableFlag")
        fun startAlarmManager(context: Context)
        {
            try {
                val intent = Intent(context, ClearCacheService::class.java)
                intent.action = "${Constants.REMOVE_FILES_SERVICES_NOTE_ID}"
                val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
                val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.IMAGES_LIFETIME, pendingIntent)
            }
            catch (e: Exception)
            {
                Log.e("CreateAlarmManagerError", "Create AlarmManager for ClearCacheServiceError: ", e)
            }
        }

        fun startService(context: Context)
        {
            Thread {
                try {
                    //Thread.sleep(3000);
                    val intent = Intent(context, ClearCacheService::class.java)
                    intent.action = "${Constants.REMOVE_FILES_SERVICES_NOTE_ID}"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context.startForegroundService(intent)
                    else
                        context.startService(intent)
                }
                catch (e: Exception)
                {
                    Log.e("StartServiceError", "Start ClearCacheSerice error: ", e)
                }
            }.start()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notification = createNotification("Clearing cache!")
                this.startForeground(Constants.REMOVE_FILES_SERVICES_NOTE_ID, notification)
            }
        } catch (e: Exception) {
            Log.e("StartServiceError", "Running Clear Cache Service Error", e)
        }
        removeOldImages()
        removeNotification()
        return START_STICKY
    }

    private fun removeOldImages() {
        try {
            val folder = File(Constants.getImageDirectory())
            if (folder.listFiles() != null && folder.exists()) {
                val now = Date()
                for (f in Objects.requireNonNull(folder.listFiles())) {
                    if ((now.time - f.lastModified()) > Constants.IMAGES_LIFETIME &&
                        (f.name.endsWith(".jpg") || f.name.endsWith(".jpeg") || f.name.endsWith(".png") ||
                         f.name.endsWith(".JPG") || f.name.endsWith(".JPEG") || f.name.endsWith(".PNG")))
                    {
                        println("NEED TO REMOVE: " + f.absolutePath)
                        f.delete()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("RemoveOldFilesError", "Remove old preview vehicle files error: ", e)
        }
    }

    private fun createNotification(text: String): Notification
    {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "${Constants.REMOVE_FILES_SERVICES_NOTE_ID}")
            .setSmallIcon(R.drawable.clear_cache_icon)
            .setContentTitle("Please wait")
            .setContentText(text)
            .setProgress(0, 0, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                "${Constants.REMOVE_FILES_SERVICES_NOTE_ID}",
                "Clear cache files notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(Constants.REMOVE_FILES_SERVICES_NOTE_ID, builder.build())
        }

        return builder.build()
    }

    private fun removeNotification() {
        try {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(Constants.REMOVE_FILES_SERVICES_NOTE_ID)
            stopService(Intent(this, ClearCacheService::class.java))
        } catch (e: java.lang.Exception) {
            Log.e("RemoveNotificationError", "Remove Clear Cache Service Notification Error: ", e)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d("BindService", "ON BIND SERVICE! $intent")
        return null
    }

    /*override fun onRebind(intent: Intent)
    {
        println("ON REBIND SERVICE! $intent")
    }

    override fun onUnbind(intent: Intent): Boolean
    {
        println("ON UNBIND SERVICE: $intent")
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
    }*/
}

package com.example.semmhosapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.semmhosapp.model.Action
import com.example.semmhosapp.model.TimetableAtCamp
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationHelper {
    val CHANNEL_ID = "Semhoz"
    val NOTIFICATION_TITLE = "Semhoz"
    val CHANNEL_NAME = "Semhoz"
    val CHANNEL_DESCRIPTION = "Semhoz"
    lateinit var context: Context
    fun init(context: Context) {this.context = context}

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(actionId: Int, actionName: String){
        createNotificationChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(actionName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.notification_icon_background)
            .build()
        NotificationManagerCompat.from(context).notify(actionId, notification)
    }

    fun setNotifications(timetableAtCamp: TimetableAtCamp){
        deleteWorkRequests()
        val idList = mutableListOf<String>()
        for (day in timetableAtCamp.daysOfCamp)
            for(action in day.actions){
                val id =setDelayNotification(action.time, day.day, action)
                id?.let {
                    idList.add(it)
                }
            }
        saveIdList(idList)
    }

    private fun deleteWorkRequests() {
        val listId = loadIdList()
        for (id in listId){
            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(id))
        }
    }

    private fun saveIdList(idList: List<String>) { //сохранение списка id в память телефона
        val json = Json(JsonConfiguration.Stable).stringify(String.serializer().list, idList)
        val pref = context.getSharedPreferences("idList", Context.MODE_PRIVATE)
        pref.edit().putString("idList", json).apply()
    }

    private fun loadIdList(): List<String>{//чтение  списка id из памяти телефона
        val pref = context.getSharedPreferences("idList", Context.MODE_PRIVATE)
        val json = pref.getString("idList", "")
        return if (json != "")
            Json(JsonConfiguration.Stable).parse(String.serializer().list, json)
        else
            emptyList()
    }

    fun setDelayNotification(time: LocalTime, date: LocalDate, action: Action): String?{
        val now = LocalDateTime.now()
        val actionTime = LocalDateTime.of(date, time)
        if (actionTime.isAfter(now)) {
            val workRequest = OneTimeWorkRequest.Builder(NotificationJob::class.java)
                .setInputData(
                    workDataOf(
                        "id" to action.id,
                        "actionName" to action.name
                    )
                )
                .setInitialDelay(getDelay(date, time), TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
            return workRequest.id.toString()
        }
            return null
    }

    private fun getDelay(date: LocalDate, time: LocalTime): Long {
        val now = LocalDateTime.now()
        val time = LocalDateTime.of(date, time)
        return Duration.between(now, time).seconds
    }

    class NotificationJob(val context: Context, params: WorkerParameters): Worker(context, params){
        override fun doWork(): Result {
            val id = inputData.getInt("id", 0)
            val name = inputData.getString("actionName")
            name?.let {
                showNotification(id, name)
            }
            return Result.success()
        }

    }
}
package com.example.semmhosapp.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.semmhosapp.model.Action
import com.example.semmhosapp.model.TimetableAtCamp
import java.time.LocalDate
import java.time.LocalTime

object NotificationHelper {
    val CHANNEL_ID = "Semhoz"
    val NOTIFICATION_TITLE = "Semhoz"
    fun showNotification(actionId: Int, actionName: String, context: Context){
        var notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(actionName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(actionId, notification)
    }

    fun setNotifications(timetableAtCamp: TimetableAtCamp){

    }

    fun setDelayNotification(time: LocalTime, date: LocalDate, action: Action, context: Context){

    }

    class NotificationJob(val context: Context, params: WorkerParameters): Worker(context, params){
        override fun doWork(): Result {
            val id = inputData.getInt("id", 0)
            val name = inputData.getString("actionName")
            name?.let {
                showNotification(id, name, context)
            }
            return Result.success()
        }

    }
}
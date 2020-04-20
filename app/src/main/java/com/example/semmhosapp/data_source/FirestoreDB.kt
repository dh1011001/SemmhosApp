package com.example.semmhosapp.data_source

import androidx.lifecycle.MutableLiveData
import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.model.ExcerptSchedule
import com.example.semmhosapp.model.ExcerptScheduleItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

object FirestoreDB {
    val schedule =  MutableLiveData<ExcerptSchedule>()
    val db = Firebase.firestore
    fun insertScheduleInDB(schedule: ExcerptSchedule){
        for (item in schedule.items){
            val data = hashMapOf("date" to item.date.toString(),
                "freeReading" to item.freeReadingExcerptAddress,
                "groupReading" to item.groupReadingExcerptAddress)
            db.collection("Excerpts").document(item.date.toString())
                .set(data)
                .addOnSuccessListener {  }
                .addOnFailureListener{  }
        }
    }

    fun createDBListener() {
        val db = Firebase.firestore
        db.collection("Excerpts").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(querySnapshot != null){
                val list = arrayListOf<ExcerptScheduleItem>()
                for (item in querySnapshot){
                    val date = item.getString("date")
                    val freeReading = item.getField<BibleExcerptAddress>("freeReading")
                    val groupReading = item.getField<BibleExcerptAddress>("groupReading")
                    if(date != null && freeReading != null && groupReading != null) {
                        val excerptScheduleItem = ExcerptScheduleItem(LocalDate.parse(date), freeReading, groupReading)
                        list.add(excerptScheduleItem)
                    }
                    schedule.value = ExcerptSchedule(list)
                }
            }
        }
    }
}
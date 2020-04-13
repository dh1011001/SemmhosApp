package com.example.semmhosapp.utils

import androidx.lifecycle.MutableLiveData
import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.model.ExcerptSchedule
import com.example.semmhosapp.model.ExcerptScheduleItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

fun getDefaultSchedule () : ExcerptSchedule {
    val item1 = ExcerptScheduleItem(
        LocalDate.now(),
        BibleExcerptAddress("Old", 1,1, 1,30),
        BibleExcerptAddress("Old", 1,1, 10,20)
    )
    val item2 = ExcerptScheduleItem(
        LocalDate.now().plusDays(1),
        BibleExcerptAddress("Old", 1,1, 4,9),
        BibleExcerptAddress("Old", 1,1, 21,41)
    )
    return ExcerptSchedule(arrayListOf(item1, item2))
}

val schedule =  MutableLiveData<ExcerptSchedule>()

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




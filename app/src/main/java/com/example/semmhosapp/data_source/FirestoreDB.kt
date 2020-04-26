package com.example.semmhosapp.data_source

import androidx.lifecycle.MutableLiveData
import com.example.semmhosapp.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime

object FirestoreDB {

/*    class Action(
        val id: Int,
        val time: LocalTime,
        val action : String
    )*/


    class ActionWrapper(action: Action = Action()){
        val id: Int = action.id
        val name: String = action.name
        val time: String = action.time.toString()
    }

    class ActionWrapperList(val actions: List<ActionWrapper> = emptyList())

    val excerptSchedule =  MutableLiveData<ExcerptSchedule>()
    val timetableAtCamp =  MutableLiveData<TimetableAtCamp>()
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

    fun createDBExcerptListener() {
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
                    excerptSchedule.value = ExcerptSchedule(list)
                }
            }
        }
    }
    
    fun insertTimetableAtDb(timetableAtCamp: TimetableAtCamp){
        for (item in timetableAtCamp.daysOfCamp){
            val actions = item.actions.map { ActionWrapper(it) }
            val actionWrapperList = ActionWrapperList(actions)
            db.collection("CampTimetable").document(item.day.toString())
                .set(actionWrapperList)
        }
    }
    fun createDBTimetableListener(){
        db.collection("CampTimetable")
            .addSnapshotListener{timetableAtDays, firebaseFirestoreException ->
                timetableAtDays?.let{
                    val result = arrayListOf<TimetableAtDay>()
                    for(timetableAtDay in timetableAtDays){
                        val actionWrapperList = timetableAtDay.toObject(ActionWrapperList::class.java)
                        val actions = actionWrapperList.actions.map { Action(
                            it.id,
                            LocalTime.parse(it.time),
                            it.name
                        ) }
                        result.add(TimetableAtDay(LocalDate.parse(timetableAtDay.id), actions))
                    }
                    timetableAtCamp.value = TimetableAtCamp(result)
                }
            }
    }

}
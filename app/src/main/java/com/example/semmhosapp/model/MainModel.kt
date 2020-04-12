package com.example.semmhosapp.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetTime

class BibleExcerptAddress(
    val testament : String,
    val book : Int,
    val chapter : Int,
    val startVerse : Int,
    val endVerse : Int

)

class ExcerptScheduleItem(
    val date : LocalDate,
    val freeReadingExcerptAddress: BibleExcerptAddress,
    val groupReadingExcerptAddress: BibleExcerptAddress

)

class ExcerptSchedule(
    val items : List<ExcerptScheduleItem>
){
    fun getCurrentDayItem() = items.find { it.date == LocalDate.now() }
    fun getItemByDate(date: LocalDate) = items.find { it.date == date }
}


class Action(
    val time: LocalTime,
    val action : String
)

class TimetableAtDay(
    val day: MonthDay,
    val actions : List<Action>
)

class TimetableAtCamp(
    val daysOfCamp: List<TimetableAtDay>
)
















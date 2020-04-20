package com.example.semmhosapp.model

import java.time.LocalDate

class BibleExcerptAddress(
    val testament : String = "",
    val book : Int = 0,
    val chapter : Int = 0,
    val startVerse : Int = 0,
    val endVerse : Int = 0

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
package com.example.semmhosapp.model

import java.time.LocalDate

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
}

//items.find { it.date == LocalDate.now() }

package com.example.semmhosapp.data_source

import com.example.semmhosapp.model.*
import java.time.LocalDate
import java.time.LocalTime

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

fun getMockDaysSchedule(): TimetableAtCamp {
    val todaySchedule = TimetableAtDay(
        LocalDate.now(),
        arrayListOf(
            Action(3, LocalTime.of(11,30), "Подъем"),
            Action(4, LocalTime.of(23,30), "Отбой")
        )
    )
    return TimetableAtCamp(arrayListOf(todaySchedule))
}
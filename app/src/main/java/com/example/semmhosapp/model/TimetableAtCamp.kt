package com.example.semmhosapp.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetTime


class Action(
    val id: Int = 0,
    val time: LocalTime = LocalTime.now(),
    val name : String = ""
)

class TimetableAtDay(
    val day: LocalDate,
    val actions : List<Action>
)

class TimetableAtCamp(
    val daysOfCamp: List<TimetableAtDay>
) {
    fun getTableAtDay(day : LocalDate) =
         daysOfCamp.find { it.day == day }

}
















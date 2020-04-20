package com.example.semmhosapp.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetTime


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
















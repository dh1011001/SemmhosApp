package com.example.semmhosapp.utils

import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.model.ExcerptSchedule
import com.example.semmhosapp.model.ExcerptScheduleItem
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
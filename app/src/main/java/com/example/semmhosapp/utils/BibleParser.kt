package com.example.semmhosapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.semmhosapp.R
import com.example.semmhosapp.model.BibleExcerptAddress
import org.xmlpull.v1.XmlPullParser

class BibleItem(val title: String, val code: String, val childs: List<BibleItem>? = null)

object BibleParser {
    val oldTestamentBooks = listOf(
        BibleItem("Быт",  "1"),
        BibleItem("Исх",  "2"),
        BibleItem("Лев",  "3"),
        BibleItem("Чис",  "4"),
        BibleItem("Втор",  "5"),
        BibleItem("Нав",  "6"),
        BibleItem("Суд",  "7"),
        BibleItem("Руф",  "8"),
        BibleItem("1Цар",  "9"),
        BibleItem("2Цар",  "10"),
        BibleItem("3Цар",  "11"),
        BibleItem("4Цар",  "12"),
        BibleItem("1Пар",  "13"),
        BibleItem("2Пар",  "14"),
        BibleItem("Езд",  "15"),
        BibleItem("Нее",  "16"),
        BibleItem("Есф",  "17"),
        BibleItem("Иов",  "18"),
        BibleItem("Пс",  "19"),
        BibleItem("Прит",  "20"),
        BibleItem("Екк",  "21"),
        BibleItem("Песн",  "22"),
        BibleItem("Ис",  "23"),
        BibleItem("Иер",  "24"),
        BibleItem("Плач",  "25"),
        BibleItem("Иез",  "26"),
        BibleItem("Дан",  "27"),
        BibleItem("Ос",  "28"),
        BibleItem("Иоил",  "29"),
        BibleItem("Амос",  "30"),
        BibleItem("Авд",  "31"),
        BibleItem("Иона",  "32"),
        BibleItem("Мих",  "33"),
        BibleItem("Наум",  "34"),
        BibleItem("Авв",  "35"),
        BibleItem("Соф",  "36"),
        BibleItem("Агг",  "37"),
        BibleItem("Зах",  "38"),
        BibleItem("Мал",  "39")

        )
    val newTestamentBooks = listOf(
        BibleItem("Мф",  "40"),
        BibleItem("Мк",  "41"),
        BibleItem("Лк",  "42"),
        BibleItem("Ин",  "43"),
        BibleItem("Деян",  "44"),
        BibleItem("Иак",  "45"),
        BibleItem("1 Пет",  "46"),
        BibleItem("2 Пет",  "47"),
        BibleItem("1 Ин",  "48"),
        BibleItem("2 Ин",  "49"),
        BibleItem("3 Ин",  "50"),
        BibleItem("Иуд",  "51"),
        BibleItem("Рим",  "52"),
        BibleItem("1 Кор",  "53"),
        BibleItem("2 Кор",  "54"),
        BibleItem("Гал",  "55"),
        BibleItem("Еф",  "56"),
        BibleItem("Флп",  "57"),
        BibleItem("Кол",  "58"),
        BibleItem("1 Фес",  "59"),
        BibleItem("2 Фес",  "60"),
        BibleItem("1 Тим",  "61"),
        BibleItem("2 Тим",  "62"),
        BibleItem("Тит",  "63"),
        BibleItem("Флм",  "64"),
        BibleItem("Евр",  "65"),
        BibleItem("Откр",  "66")

        )

    val testaments = listOf(
        BibleItem("Ветхий завет",  "Old",oldTestamentBooks),
        BibleItem("Новый завет",  "New", newTestamentBooks)
    )


    fun getBibleExcerpt (context: Context, address: BibleExcerptAddress) : List<String>?{
        val list = ArrayList<String>()

        var fb = false
        var fc = false
        var fv = false
        var fz = false

        try {
            val parser: XmlPullParser = context.getResources().getXml(R.xml.bible)
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                val TAG = "ЛогКот"
                var tmp = ""
                if (parser.eventType == XmlPullParser.START_TAG && parser.attributeCount > 0) {
                    if (parser.name == "testament") {
                        fz = parser.getAttributeValue(0) == address.testament
                    }
                    if (parser.name == "book") {
                        fb = parser.getAttributeValue(0).toInt() == address.book
                    }
                    if (parser.name == "chapter") {
                        fc = parser.getAttributeValue(0).toInt() == address.chapter
                    }
                    if (parser.name == "verse") {
                        if (parser.getAttributeValue(0).toInt() == address.startVerse) {
                            fv = true
                        }
                    }
                    if (fz && fb && fc && fv) {
                        if (parser.name == "verse") {
                            if (parser.getAttributeValue(0).toInt() <= address.endVerse) {
                                tmp = "${parser.getAttributeValue(0)} "
                                parser.next()
                                tmp += parser.text


                                list.add(tmp)
                            } else {
                                Log.d("test", list.toString())
                                return list
                            }
                        }
                    }
                }
                parser.next()
            }
        } catch (t: Throwable) {
            Toast.makeText(context,
                "Ошибка при загрузке XML-документа: $t",
                Toast.LENGTH_LONG).show()
        }
        return null
    }
}
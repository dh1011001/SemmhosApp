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
        BibleItem("Ион",  "31"),
        BibleItem("Мих",  "32")
    )
    val newTestamentBooks = listOf(
        BibleItem("Иоан",  "42"),
        BibleItem("Деян",  "43")
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
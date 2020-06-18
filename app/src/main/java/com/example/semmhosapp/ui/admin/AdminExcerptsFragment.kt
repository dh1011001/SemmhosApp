package com.example.semmhosapp.ui.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.semmhosapp.R
import com.example.semmhosapp.data_source.FirestoreDB
import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.model.ExcerptScheduleItem
import com.example.semmhosapp.ui.bible_excerpt.BibleExerptFragment
import com.example.semmhosapp.ui.common.SelectDateFragment
import com.example.semmhosapp.utils.BibleParser
import kotlinx.android.synthetic.main.fragment_admin_excerpts.*
import kotlinx.android.synthetic.main.fragment_admin_excerpts.view.*
import java.time.LocalDate


class AdminExcerptFragment : SelectDateFragment(){
    fun setVisible(){
        root.FRbook.visibility = if (root.freeReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.GRbook.visibility = if (root.groupReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.FRchapter.visibility = if (root.freeReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.GRchapter.visibility = if (root.groupReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.FRstartVerse.visibility = if (root.freeReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.GRstartVerse.visibility = if (root.groupReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.FRendVerse.visibility = if (root.freeReading.isChecked) View.VISIBLE else View.INVISIBLE
        root.GRendVerse.visibility = if (root.groupReading.isChecked) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_admin_excerpts, container, false)
        setHasOptionsMenu(true)
        onSelectDate()
        showPrewExcerpt()
        setVisible()
        root.previewExcerpt.setEnabled(false)
        root.previewExcerpt.setFocusable(false)
        root.buttonSave.setOnClickListener {
            if ((checkFR()||isEmptyFR()) && (checkGR()||isEmptyGR())) {

                val excerptScheduleItem = ExcerptScheduleItem(
                    selectedDate,
                    if (!isEmptyFR())(getAddresesFromAdmin(
                        root.FRbook.text.toString(),
                        root.FRchapter.text.toString().toInt(),
                        root.FRstartVerse.text.toString().toInt(),
                        root.FRendVerse.text.toString().toInt()
                        )) else null,
                    if (!isEmptyGR())(getAddresesFromAdmin(
                        root.GRbook.text.toString(),
                        root.GRchapter.text.toString().toInt(),
                        root.GRstartVerse.text.toString().toInt(),
                        root.GRendVerse.text.toString().toInt()
                    )) else null
                )
                FirestoreDB.insertExcerptAtDay(excerptScheduleItem)

            } else
                Toast.makeText(requireContext(), "Неожиданные данные", Toast.LENGTH_LONG).show()
        }
        root.buttonClean.setOnClickListener {
            clearAdminSetings()
        }
        root.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                setVisible()
                showPrewExcerpt()
        }
        return root
    }


    fun showPrewExcerpt(){
        fun getPreviewText(address: BibleExcerptAddress): String{
            return if (address != null) {
                val redingList =
                    BibleParser.getBibleExcerpt(requireContext(), address)
                if (redingList != null) {
                    var bofResultStr = ""
                    for (text in redingList) {
                        bofResultStr += text + "\n"
                    }
                    bofResultStr
                } else
                    ""
            } else
                ""
        }
        if(checkFR()) {
            val freeRedingAdress = getAddresesFromAdmin(
                root.FRbook.text.toString(),
                root.FRchapter.text.toString().toInt(),
                root.FRstartVerse.text.toString().toInt(),
                root.FRendVerse.text.toString().toInt()
            );
            root.previewExcerpt.setText(getPreviewText(freeRedingAdress))
        }else
            root.previewExcerpt.setText("Чек не прошел")
        if(checkGR()) {
            val groupRedingAdress = getAddresesFromAdmin(
                root.GRbook.text.toString(),
                root.GRchapter.text.toString().toInt(),
                root.GRstartVerse.text.toString().toInt(),
                root.GRendVerse.text.toString().toInt()
            );
            root.previewExcerpt.setText(getPreviewText(groupRedingAdress))
        }else
            root.previewExcerpt.setText("Чек не прошел")
    }

    fun getAddresesFromAdmin(book: String, chapter: Int, startVerse: Int, endVerse: Int) : BibleExcerptAddress{
        val testament: String
        val bookCode: Int
        if (BibleParser.oldTestamentBooks.find{it.title == book} != null) {
            testament = "Old"
            bookCode = BibleParser.oldTestamentBooks.find{it.title == book}!!.code.toInt()
            }
        else if(BibleParser.newTestamentBooks.find{it.title == book} != null){
            testament = "New"
            bookCode = BibleParser.newTestamentBooks.find{it.title == book}!!.code.toInt()
        }
        else throw IllegalStateException ("Книга не найдена, че-то странное")
        return BibleExcerptAddress(testament, bookCode,chapter, startVerse, endVerse)
    }


    override fun onSelectDate() {
        root.dateTextView.setText("Текст на " + selectedDate.toString())

        val freeRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
        if (freeRedingAdress != null){
            root.FRbook.setText(makeBookStr(freeRedingAdress.book))
            root.FRchapter.setText(freeRedingAdress.chapter.toString())
            root.FRstartVerse.setText(freeRedingAdress.startVerse.toString())
            root.FRendVerse.setText(freeRedingAdress.endVerse.toString())

        } else {
            clearAdminSetings()

        }

        val groupRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
        if (groupRedingAdress != null){
            root.GRbook.setText(makeBookStr(groupRedingAdress.book))
            root.GRchapter.setText(groupRedingAdress.chapter.toString())
            root.GRstartVerse.setText(groupRedingAdress.startVerse.toString())
            root.GRendVerse.setText(groupRedingAdress.endVerse.toString())
        } else {

            clearAdminSetings()

        }

        showPrewExcerpt()

    }

    private fun clearAdminSetings() {
        root.FRbook.setText("")
        root.FRchapter.setText("")
        root.FRendVerse.setText("")
        root.FRstartVerse.setText("")

        root.GRbook.setText("")
        root.GRchapter.setText("")
        root.GRstartVerse.setText("")
        root.GRendVerse.setText("")

        root.previewExcerpt.setText("")

        root.previewExcerpt.setText("Тут пусто")
    }


    fun checkFR() =(BibleParser.oldTestamentBooks.find { it.title == root.FRbook.text.toString() } != null ||
                BibleParser.newTestamentBooks.find { it.title == root.FRbook.text.toString() } != null) &&
                root.FRchapter.text.toString().isNotEmpty()&& root.FRstartVerse.text.toString().isNotEmpty() && root.FRendVerse.text.toString().isNotEmpty()

    fun checkGR() =(BibleParser.oldTestamentBooks.find { it.title == root.GRbook.text.toString() } != null ||
            BibleParser.newTestamentBooks.find { it.title == root.GRbook.text.toString() } != null) &&
            root.GRchapter != null && root.GRstartVerse != null && root.GRendVerse != null


    fun isEmptyGR() = (root.GRbook.text.isEmpty() && root.GRchapter.text.isEmpty() &&
                        root.GRstartVerse.text.isEmpty() && root.GRendVerse.text.isEmpty())

    fun isEmptyFR() = (root.FRbook.text.isEmpty() && root.FRchapter.text.isEmpty() &&
            root.FRstartVerse.text.isEmpty() && root.FRendVerse.text.isEmpty())

}
fun makeBookStr(book: Int): String =  BibleParser.oldTestamentBooks.find{it.code.toInt() == book}?.title ?:
    BibleParser.newTestamentBooks.find{it.code.toInt() == book}?.title ?: ""

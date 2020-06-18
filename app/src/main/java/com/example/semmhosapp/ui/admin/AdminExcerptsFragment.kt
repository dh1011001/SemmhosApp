package com.example.semmhosapp.ui.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.navArgs
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
    val GREdits: List<EditText> by lazy{ listOf(root.GRbook, root.GRchapter, root.GRstartVerse, root.GRendVerse)}
    val FREdits: List<EditText> by lazy{ listOf(root.FRbook, root.FRchapter, root.FRstartVerse, root.FRendVerse)}
    fun getEdits() = GREdits + FREdits

    fun setDate(){
        root.dateTextView.setText("Текст на " + selectedDate.toString())
    }

    fun setVisible(){
        FREdits.forEach { it.visibility =  if (root.freeReading.isChecked) View.VISIBLE else View.INVISIBLE }
        GREdits.forEach { it.visibility =  if (root.groupReading.isChecked) View.VISIBLE else View.INVISIBLE }
    }

    fun updatePreview() {
        if (!currentIsEmpty()) {
            if (checkCurrent()) {
                val address = if (root.freeReading.isChecked)
                    getAddresesFromAdmin(
                        root.FRbook.text.toString(),
                        root.FRchapter.text.toString().toInt(),
                        root.FRstartVerse.text.toString().toInt(),
                        root.FRendVerse.text.toString().toInt()
                    )
                else{
                    getAddresesFromAdmin(
                        root.GRbook.text.toString(),
                        root.GRchapter.text.toString().toInt(),
                        root.GRstartVerse.text.toString().toInt(),
                        root.GRendVerse.text.toString().toInt()
                    );
                }
                if (address != null)
                    root.previewExcerpt.setText(BibleParser.getBibleExcerpt(requireContext(), address))
                else
                    root.previewExcerpt.setText("Ошибка!")

            } else
                root.previewExcerpt.setText("Чек не пройден")
        } else
            root.previewExcerpt.setText("")
    }

    fun setEditTexts(){
        val freeRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
        if (freeRedingAdress != null){
            root.FRbook.setText(BibleParser.makeBookStr(freeRedingAdress.book))
            root.FRchapter.setText(freeRedingAdress.chapter.toString())
            root.FRstartVerse.setText(freeRedingAdress.startVerse.toString())
            root.FRendVerse.setText(freeRedingAdress.endVerse.toString())
        } else
            clearAdminSetings()

        val groupRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
        if (groupRedingAdress != null){
            root.GRbook.setText(BibleParser.makeBookStr(groupRedingAdress.book))
            root.GRchapter.setText(groupRedingAdress.chapter.toString())
            root.GRstartVerse.setText(groupRedingAdress.startVerse.toString())
            root.GRendVerse.setText(groupRedingAdress.endVerse.toString())
        } else
            clearAdminSetings()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_admin_excerpts, container, false)
        setHasOptionsMenu(true)
        root.previewExcerpt.setEnabled(false)
        root.previewExcerpt.setFocusable(false)
        root.buttonSave.setOnClickListener {
            updatePreview()
            save()
        }
        root.buttonClean.setOnClickListener {
            clearAdminSetings()
        }
        root.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            setVisible()
            updatePreview()
        }
        setDate()
        setEditTexts()
        setVisible()
        updatePreview()
        return root
    }

    fun save(){
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


    fun getAddresesFromAdmin(book: String, chapter: Int, startVerse: Int, endVerse: Int) : BibleExcerptAddress?{
        val bookCode = BibleParser.makeBookInt(book)
        val testament = BibleParser.testamentByBook(book)
        return if (bookCode != null && testament != null)
            BibleExcerptAddress(testament, bookCode,chapter, startVerse, endVerse)
        else
            null
    }


    override fun onSelectDate() {
        setDate()
        setEditTexts()
        updatePreview()
    }

    private fun clearAdminSetings() {
        getEdits().forEach { it.setText("") }
    }

    fun checkFR(): Boolean =(BibleParser.oldTestamentBooks.find { it.title == root.FRbook.text.toString() } != null ||
                BibleParser.newTestamentBooks.find { it.title == root.FRbook.text.toString() } != null) &&
                root.FRchapter.text.toString().isNotEmpty()&& root.FRstartVerse.text.toString().isNotEmpty() && root.FRendVerse.text.toString().isNotEmpty()

    fun checkGR(): Boolean =(BibleParser.oldTestamentBooks.find { it.title == root.GRbook.text.toString() } != null ||
            BibleParser.newTestamentBooks.find { it.title == root.GRbook.text.toString() } != null) &&
            root.GRchapter != null && root.GRstartVerse != null && root.GRendVerse != null

    fun checkCurrent(): Boolean = if (root.freeReading.isChecked) checkFR() else checkGR()

    fun isEmptyGR(): Boolean = GREdits.all { it.text.isEmpty() }
    fun isEmptyFR(): Boolean = FREdits.all { it.text.isEmpty() }

    fun currentIsEmpty(): Boolean = if (root.freeReading.isChecked) isEmptyFR() else isEmptyGR()

}


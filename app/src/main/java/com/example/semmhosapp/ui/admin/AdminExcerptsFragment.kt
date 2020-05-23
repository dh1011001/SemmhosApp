package com.example.semmhosapp.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.semmhosapp.R
import com.example.semmhosapp.data_source.FirestoreDB
import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.model.ExcerptScheduleItem
import com.example.semmhosapp.ui.common.SelectDateFragment
import com.example.semmhosapp.utils.BibleParser
import kotlinx.android.synthetic.main.fragment_admin_excerpts.*
import kotlinx.android.synthetic.main.fragment_admin_excerpts.view.*
import java.time.LocalDate


class AdminExcerptFragment : SelectDateFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_admin_excerpts, container, false)
        setHasOptionsMenu(true)
        onSelectDate()

        root.FRbook.visibility = View.INVISIBLE
        root.GRbook.visibility = View.VISIBLE
        root.FRchapter.visibility = View.INVISIBLE
        root.GRchapter.visibility = View.VISIBLE
        root.FRstartVerse.visibility = View.INVISIBLE
        root.GRstartVerse.visibility = View.VISIBLE
        root.FRendVerse.visibility = View.INVISIBLE
        root.GRendVerse.visibility = View.VISIBLE

        root.previewExcerpt.setEnabled(false)
        root.previewExcerpt.setFocusable(false)

        root.buttonSave.setOnClickListener {

            if(checkFR() && checkGR()) {
                val excerptScheduleItem = ExcerptScheduleItem(
                    selectedDate,
                    getAddresesFromAdminFR(),
                    getAddresesFromAdminGR()
                )
                FirestoreDB.insertExcerptAtDay(excerptScheduleItem)
            } else Toast.makeText(requireContext(), "Неожиданные данные", Toast.LENGTH_LONG).show()
        }



        root.radioGroup.setOnCheckedChangeListener{group, checkedId ->
            if(root.freeReading.isChecked){
                root.FRbook.visibility = View.VISIBLE
                root.GRbook.visibility = View.INVISIBLE
                root.FRchapter.visibility = View.VISIBLE
                root.GRchapter.visibility = View.INVISIBLE
                root.FRstartVerse.visibility = View.VISIBLE
                root.GRstartVerse.visibility = View.INVISIBLE
                root.FRendVerse.visibility = View.VISIBLE
                root.GRendVerse.visibility = View.INVISIBLE

                if(checkFR()) {
                    val freeRedingAdress = getAddresesFromAdminFR();
                    if (freeRedingAdress != null) {
                        val freeRedingList =
                            BibleParser.getBibleExcerpt(requireContext(), freeRedingAdress)
                        if (freeRedingList != null) {
                            var bofResultStr = ""
                            for (text in freeRedingList) {
                                bofResultStr += text + "\n"
                            }
                            root.previewExcerpt.setText(bofResultStr);

                        }
                    }
                }
            } else{
                if(checkGR()) {
                    val groupRedingAdress = getAddresesFromAdminGR();
                    if (groupRedingAdress != null) {
                        val groupRedingList =
                            BibleParser.getBibleExcerpt(requireContext(), groupRedingAdress)
                        if (groupRedingList != null) {
                            var bofResultStr = ""
                            for (text in groupRedingList) {
                                bofResultStr += text + "\n"
                            }
                            root.previewExcerpt.setText(bofResultStr);

                        }
                    }
                }
                root.FRbook.visibility = View.INVISIBLE
                root.GRbook.visibility = View.VISIBLE
                root.FRchapter.visibility = View.INVISIBLE
                root.GRchapter.visibility = View.VISIBLE
                root.FRstartVerse.visibility = View.INVISIBLE
                root.GRstartVerse.visibility = View.VISIBLE
                root.FRendVerse.visibility = View.INVISIBLE
                root.GRendVerse.visibility = View.VISIBLE
            }
        }
        return root
    }

    fun getAddresesFromAdminGR() : BibleExcerptAddress{
        var book = root.GRbook.text.toString() //"Ин"
        var testament: String
        var bookCode: Int
        if (BibleParser.oldTestamentBooks.find{it.title == book} != null) {
            testament = "Old"
            bookCode = BibleParser.oldTestamentBooks.find{it.title == book}!!.code.toInt()
            }
        else if(BibleParser.newTestamentBooks.find{it.title == book} != null){
            testament = "New"
            bookCode = BibleParser.newTestamentBooks.find{it.title == book}!!.code.toInt()
        }
        else throw IllegalStateException ("Книга не найдена, че-то странное")

        val chapter = root.GRchapter.text.toString().toInt()
        val startVerse = root.GRstartVerse.text.toString().toInt()
        val endVerse = root.GRendVerse.text.toString().toInt()

        return BibleExcerptAddress(testament, bookCode,chapter, startVerse, endVerse)


    }
    fun getAddresesFromAdminFR() : BibleExcerptAddress{
        var book = root.FRbook.text.toString() //"Ин"
        var testament: String
        var bookCode: Int
        if (BibleParser.oldTestamentBooks.find{it.title == book}!= null) {
            testament = "Old"
            bookCode = BibleParser.oldTestamentBooks.find{it.title == book}!!.code.toInt()
        }
        else if(BibleParser.newTestamentBooks.find{it.title == book} != null){
            testament = "New"
            bookCode = BibleParser.newTestamentBooks.find{it.title == book}!!.code.toInt()
        }
        else throw IllegalStateException ("Книга не найдена, че-то странное")

        val chapter = root.FRchapter.text.toString().toInt()
        val startVerse = root.FRstartVerse.text.toString().toInt()
        val endVerse = root.FRendVerse.text.toString().toInt()

        return BibleExcerptAddress(testament, bookCode,chapter, startVerse, endVerse)


    }

    override fun onSelectDate() {
        root.dateTextView.setText("Текст на " + selectedDate.toString())
    }


    fun checkFR() =(BibleParser.oldTestamentBooks.find { it.title == root.FRbook.text.toString() } != null ||
                BibleParser.newTestamentBooks.find { it.title == root.FRbook.text.toString() } != null) &&
                root.FRchapter != null && root.FRstartVerse != null && root.FRendVerse != null

    fun checkGR() =(BibleParser.oldTestamentBooks.find { it.title == root.GRbook.text.toString() } != null ||
            BibleParser.newTestamentBooks.find { it.title == root.GRbook.text.toString() } != null) &&
            root.GRchapter != null && root.GRstartVerse != null && root.GRendVerse != null

}



package com.example.semmhosapp.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.semmhosapp.R
import com.example.semmhosapp.ui.common.SelectDateFragment
import kotlinx.android.synthetic.main.fragment_admin_excerpts.view.*


class AdminExcerptFragment : SelectDateFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_admin_excerpts, container, false)
        setHasOptionsMenu(true)
        onSelectDate()
        root.buttonSave.setOnClickListener {

        }

        return root
    }



    override fun onSelectDate() {

        root.dateTextView.setText("Текст на " + selectedDate.toString())


    }

    fun catchExcerptSchedule(){

    }


}
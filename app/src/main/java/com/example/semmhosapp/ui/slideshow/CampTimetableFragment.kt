package com.example.semmhosapp.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.semmhosapp.R
import com.example.semmhosapp.ui.common.SelectDateFragment

class CampTimetableFragment : SelectDateFragment() {
    override fun onSelectDate() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_camp_timetable, container, false)
        setHasOptionsMenu(true)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        return root
    }
}

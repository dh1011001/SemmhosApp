package com.example.semmhosapp.ui.camp_timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semmhosapp.R
import com.example.semmhosapp.data_source.getMockDaysSchedule
import com.example.semmhosapp.ui.common.SelectDateFragment
import kotlinx.android.synthetic.main.fragment_camp_timetable.view.*

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
        root.recytlerView.layoutManager = LinearLayoutManager(requireContext())
        getMockDaysSchedule().getTableAtDay(selectedDate)?.let{
            root.recytlerView.adapter = TimetableAdapter(it)
        }
        return root
    }
}

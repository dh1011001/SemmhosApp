package com.example.semmhosapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.semmhosapp.R
import com.example.semmhosapp.data_source.FirestoreDB
import com.example.semmhosapp.data_source.getDefaultSchedule
import com.example.semmhosapp.data_source.getMockDaysSchedule
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.insertMockExcerptsButton.setOnClickListener {
            FirestoreDB.insertScheduleInDB(getDefaultSchedule())
        }
        root.insertMockTimeTableButton.setOnClickListener {
            FirestoreDB.insertTimetableAtDb(getMockDaysSchedule())
        }
        return root
    }
}

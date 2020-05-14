package com.example.semmhosapp.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semmhosapp.R
import com.example.semmhosapp.data_source.FirestoreDB
import com.example.semmhosapp.model.Action
import com.example.semmhosapp.model.TimetableAtDay
import com.example.semmhosapp.ui.camp_timetable.TimetableAdapter
import com.example.semmhosapp.ui.common.SelectDateFragment
import kotlinx.android.synthetic.main.fragment_admin_timetable.view.*
import kotlinx.android.synthetic.main.fragment_bible_excerpt_text.view.*
import kotlinx.android.synthetic.main.fragment_camp_timetable.view.*
import java.time.LocalDate
import java.time.LocalTime

class   AdminTimetableFragment : SelectDateFragment(), TimetableAdapter.Listener{

    override fun onSelectDate() {
        actions = FirestoreDB.timetableAtCamp.value?.getTableAtDay(selectedDate)?.actions?.toMutableList() ?: mutableListOf()
        updateReсyclerView()

    }

    lateinit var actions: MutableList<Action>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_admin_timetable, container, false)
        setHasOptionsMenu(true)
        FirestoreDB.timetableAtCamp.observe(viewLifecycleOwner, Observer {
            actions = it.getTableAtDay(selectedDate)?.actions?.toMutableList() ?: mutableListOf()
            updateReсyclerView()
        })
        root.timetableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return root
    }

    fun updateReсyclerView(){

        root.timetableRecyclerView.adapter= TimetableAdapter(actions, this)
    }

    override fun onDeleteItemClick(item: Action) {
        Toast.makeText(requireContext(), "delete", Toast.LENGTH_SHORT).show()
    }

    override fun onClickItem(item: Action) {
        Toast.makeText(requireContext(), "item click", Toast.LENGTH_SHORT).show()
    }
}
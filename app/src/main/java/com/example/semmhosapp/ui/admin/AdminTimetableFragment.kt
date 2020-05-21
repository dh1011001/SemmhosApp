package com.example.semmhosapp.ui.admin

import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.dialog_action.view.*
import kotlinx.android.synthetic.main.fragment_admin_timetable.view.*
import kotlinx.android.synthetic.main.fragment_bible_excerpt_text.view.*
import kotlinx.android.synthetic.main.fragment_camp_timetable.view.*
import java.time.LocalDate
import java.time.LocalTime

class   AdminTimetableFragment : SelectDateFragment(), TimetableAdapter.Listener{
    lateinit var actions: MutableList<Action>

    override fun onSelectDate() {
        actions = FirestoreDB.timetableAtCamp.value?.getTableAtDay(selectedDate)?.actions?.toMutableList() ?: mutableListOf()
        updateReсyclerView()

    }

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
        root.addActionButton.setOnClickListener {
            showActionDialog(null)

        }

        root.saveActionsButton.setOnClickListener {
            FirestoreDB.insertDayTimetableAtDb(TimetableAtDay(selectedDate, actions))
        }

        return root
    }

    fun updateReсyclerView(){
        root.timetableRecyclerView.adapter= TimetableAdapter(actions.sortedBy { it.time }, this)
    }

    override fun onDeleteItemClick(item: Action) {
        actions.remove(item)
        updateReсyclerView()
    }

    override fun onClickItem(item: Action) {
        showActionDialog(item)
        updateReсyclerView()
    }

    fun showActionDialog(action: Action?){
        val view =LayoutInflater.from(requireContext()).inflate(R.layout.dialog_action, null)
        view.simpleTimePicker.setIs24HourView(true)
        view.simpleTimePicker.hour = action?.time?.hour ?: LocalTime.now().hour
        view.simpleTimePicker.minute = action?.time?.minute ?: LocalTime.now().minute
        view.actionNameEditText.setText(action?.name ?: "")
        AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(if (action == null) "Добавление события" else "Редактирование события")
            .setPositiveButton("OK"){dialog, which ->
                val newAction = Action(
                    0,
                    LocalTime.of(view.simpleTimePicker.hour, view.simpleTimePicker.minute),  //id поправить
                    view.actionNameEditText.text.toString()
                )
                if(action != null)
                    actions.remove(action)
                actions.add(newAction)
                updateReсyclerView()
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

}
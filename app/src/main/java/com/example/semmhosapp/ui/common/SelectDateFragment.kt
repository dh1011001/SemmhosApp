package com.example.semmhosapp.ui.common

import android.app.DatePickerDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.example.semmhosapp.MainActivity
import com.example.semmhosapp.R
import java.time.LocalDate

abstract  class SelectDateFragment: Fragment(), DatePickerDialog.OnDateSetListener {
    lateinit var root : View
    var selectedDate = LocalDate.now()
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.prevDateItem -> {
                selectedDate = selectedDate.minusDays(1)
                onSelectDate()
            }
            R.id.todayItem -> {
                selectedDate = LocalDate.now()
                onSelectDate()
            }
            R.id.nextDayItem -> {
                selectedDate = selectedDate.plusDays(1)
                onSelectDate()
            }
            R.id.pickDaytItem -> {
                DatePickerDialog(
                    requireContext(),
                    this,
                    selectedDate.year,
                    selectedDate.monthValue,
                    selectedDate.dayOfMonth

                ).show()
            }
        }
        (activity as MainActivity).supportActionBar?.title =  selectedDate.toString()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.select_date, menu)
        super.onCreateOptionsMenu(menu, inflater)
        (activity as MainActivity).supportActionBar?.title =  selectedDate.toString()
    }

    protected abstract fun onSelectDate()

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDate = LocalDate.of(year, month, dayOfMonth)
        onSelectDate()
    }
}
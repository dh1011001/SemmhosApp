package com.example.semmhosapp.ui.bible_excerpt

import android.os.Bundle
import android.view.*

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.semmhosapp.R
import com.example.semmhosapp.data_source.FirestoreDB
import com.example.semmhosapp.ui.common.SelectDateFragment

import com.example.semmhosapp.utils.BibleParser
import kotlinx.android.synthetic.main.fragment_bible_excerpt.view.*
import kotlinx.android.synthetic.main.fragment_bible_excerpt_text.view.*

import java.lang.IllegalArgumentException

class BibleExerptFragment : SelectDateFragment() {

    var freeReadingText = MutableLiveData<String>()
    var groupReadingText = MutableLiveData<String>()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_bible_excerpt, container, false)
        setHasOptionsMenu(true)
        FirestoreDB.createDBExcerptListener()
        FirestoreDB.excerptSchedule.observeForever{onSelectDate()}
        root.viewPager.adapter = ExcerptPagerAdapter(childFragmentManager)
        root.tabLayout.setupWithViewPager(root.viewPager)
        return root
    }

    override fun onSelectDate() {
        val freeRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
        if (freeRedingAdress != null){
            val freeRedingList = BibleParser.getBibleExcerpt(requireContext(), freeRedingAdress)
            if(freeRedingList != null){
                var bofResultStr = ""
                for(text in freeRedingList){
                    bofResultStr += text + "\n"
                }
                root.dateTextView.setText("Текст на " + selectedDate.toString())
                freeReadingText.value = bofResultStr

            }
        } else {
            freeReadingText.value = "Нет отрывка на данный день"
        }

        val groupRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
        if (groupRedingAdress != null){
            val groupRedingList = BibleParser.getBibleExcerpt(requireContext(), groupRedingAdress)
            if(groupRedingList != null){
                var bofResultStr = ""
                for(text in groupRedingList){
                    bofResultStr += text + "\n"
                }
                root.dateTextView.setText("Текст на " + selectedDate.toString())
                groupReadingText.value = bofResultStr

            }
        } else {
            groupReadingText.value = "Нет отрывка на данный день"
        }
    }


    class ExcerptTextFragment(val text : LiveData<String>) : Fragment(){
        lateinit var root : View
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            root = inflater.inflate(R.layout.fragment_bible_excerpt_text, container, false)

            text.observeForever{
                root.excerptTextView.text = it
            }

            return root
        }
    }

    inner class ExcerptPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> ExcerptTextFragment(freeReadingText)
                1 -> ExcerptTextFragment(groupReadingText)
                else -> throw IllegalArgumentException("Wrong position")
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Свободное чтение"
                1 -> "Групповое чтение"
                else -> throw IllegalArgumentException("Wrong position")
            }
        }
        override fun getCount() = 2

    }
}

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
import com.google.android.material.tabs.TabLayout
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
        FirestoreDB.excerptSchedule.observeForever{onSelectDate()}
        root.viewPager.adapter = ExcerptPagerAdapter(childFragmentManager)
        root.tabLayout.setupWithViewPager(root.viewPager)
        root.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab!!.position == 0){
                        val freeRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
                        freeRedingAdress?.let {
                            val bookInString = makeBookStr(it.book)
                            root.addressTextView.setText(bookInString + " " + freeRedingAdress!!.chapter)
                        }
                    }
                    if (tab!!.position == 1){
                        val groupRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
                        groupRedingAdress?.let {
                            val bookInString = makeBookStr(it.book)
                            root.addressTextView.setText(bookInString + " " + groupRedingAdress!!.chapter)
                        }

                    }

                }

            })

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

                val book = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress!!.book
                val bookInString = makeBookStr(book)

                root.addressTextView.setText(bookInString + " " + freeRedingAdress.chapter)

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
                val book = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress!!.book
                val bookInString = makeBookStr(book)

                root.addressTextView.setText(bookInString + " " + groupRedingAdress.chapter)

                root.dateTextView.setText("Текст на " + selectedDate.toString())
                groupReadingText.value = bofResultStr

            }
        } else {
            groupReadingText.value = "Нет отрывка на данный день"
        }
    }

    fun makeBookStr(book: Int) : String {
        val bookInString : String
        if (BibleParser.oldTestamentBooks.find{it.code.toInt() == book} != null) {
            bookInString = BibleParser.oldTestamentBooks.find{it.code.toInt() == book}!!.title
        }
        else if(BibleParser.newTestamentBooks.find{it.code.toInt() == book} != null){
            bookInString = BibleParser.newTestamentBooks.find{it.code.toInt() == book}!!.title
        } else{
            bookInString = ""

        }
        return bookInString
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

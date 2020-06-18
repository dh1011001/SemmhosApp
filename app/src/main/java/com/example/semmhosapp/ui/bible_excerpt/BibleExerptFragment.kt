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
import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.ui.common.SelectDateFragment

import com.example.semmhosapp.utils.BibleParser
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_bible_excerpt.view.*
import kotlinx.android.synthetic.main.fragment_bible_excerpt_text.view.*

import java.lang.IllegalArgumentException

class BibleExerptFragment : SelectDateFragment() {

    var freeReadingText = MutableLiveData<String>()
    var groupReadingText = MutableLiveData<String>()

    fun updateViews(){
        setDate()
        setExcerptText()
        setBookTitle()
    }
    fun setDate(){
        root.dateTextView.setText("Текст на " + selectedDate.toString())
    }
    fun setExcerptText(){
        fun setForAddress(address: BibleExcerptAddress?, textLiveData: MutableLiveData<String>){
            if (address != null){
                val text = BibleParser.getBibleExcerpt(activity!!, address)
                if(text != null)
                    textLiveData.value = text
                else
                    textLiveData.value = "Ошибка!"
            } else {
                textLiveData.value = "Нет отрывка на данный день"
            }
        }
        val groupRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
        setForAddress(groupRedingAdress, groupReadingText)
        val freeRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
        setForAddress(freeRedingAdress, freeReadingText)
    }

    fun setBookTitle(){
        fun setForAdress(address: BibleExcerptAddress?){
            if (address != null){
                val bookInStr = BibleParser.makeBookStr(address.book)
                root.addressTextView.text = "${bookInStr ?: "Ошибка!"} ${address.chapter}"
            }else
                root.addressTextView.text = ""
        }
        if (root.tabLayout.selectedTabPosition == 0){
            val freeRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
            setForAdress(freeRedingAdress)
        }
        if (root.tabLayout.selectedTabPosition == 1){
            val groupRedingAdress = FirestoreDB.excerptSchedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
            setForAdress(groupRedingAdress)
        }
    }

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
                    updateViews()
                }
            })
        updateViews()
        return root
    }

    override fun onSelectDate() {
        updateViews()
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

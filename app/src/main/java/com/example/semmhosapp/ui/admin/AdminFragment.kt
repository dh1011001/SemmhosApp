package com.example.semmhosapp.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.example.semmhosapp.R
import com.example.semmhosapp.ui.bible_excerpt.BibleExerptFragment
import com.example.semmhosapp.ui.common.SelectDateFragment
import kotlinx.android.synthetic.main.fragment_bible_excerpt.view.*
import java.lang.IllegalArgumentException

/**
 * A simple [Fragment] subclass.
 * 66
 */
class AdminFragment : SelectDateFragment() {
    override fun onSelectDate() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_admin, container, false)
        root.viewPager.adapter = AdminPagerAdapter(childFragmentManager)
        root.tabLayout.setupWithViewPager(root.viewPager)
        return root
    }


    inner class AdminPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> AdminExcerptFragment()
                1 -> AdminTimetableFragment()
                else -> throw IllegalArgumentException("Wrong position")
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Отрывки"
                1 -> "Расписание"
                else -> throw IllegalArgumentException("Wrong position")
            }
        }
        override fun getCount() = 2

    }
}

package com.example.semmhosapp.ui.bible_excerpt

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.semmhosapp.R
import com.example.semmhosapp.model.BibleExcerptAddress
import com.example.semmhosapp.model.ExcerptSchedule
import com.example.semmhosapp.model.ExcerptScheduleItem
import com.example.semmhosapp.utils.schedule
import kotlinx.android.synthetic.main.fragment_bible_excerpt.view.*
import kotlinx.android.synthetic.main.fragment_bible_excerpt_text.view.*
import org.xmlpull.v1.XmlPullParser
import java.lang.IllegalArgumentException
import java.time.LocalDate

class BibleExerptFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    var freeReadingText = MutableLiveData<String>()
    var groupReadingText = MutableLiveData<String>()

    lateinit var root : View

    var selectedDate = LocalDate.now()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_bible_excerpt, container, false)
        setHasOptionsMenu(true)
        schedule.observeForever{setCurrentExcerpt()}
        root.viewPager.adapter = ExcerptPagerAdapter(childFragmentManager)
        root.tabLayout.setupWithViewPager(root.viewPager)
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.prevDateItem -> {
                selectedDate = selectedDate.minusDays(1)
                setCurrentExcerpt()
            }
            R.id.todayItem -> {
                selectedDate = LocalDate.now()
                setCurrentExcerpt()
            }
            R.id.nextDayItem -> {
                selectedDate = selectedDate.plusDays(1)
                setCurrentExcerpt()
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
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.select_date, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setCurrentExcerpt() {
        val freeRedingAdress = schedule.value!!.getItemByDate(selectedDate)?.freeReadingExcerptAddress
        if (freeRedingAdress != null){
            val freeRedingList = getBibleExcerpt(freeRedingAdress)
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

        val groupRedingAdress = schedule.value!!.getItemByDate(selectedDate)?.groupReadingExcerptAddress
        if (groupRedingAdress != null){
            val groupRedingList = getBibleExcerpt(groupRedingAdress)
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

    fun getDefaultSchedule () : ExcerptSchedule {
        val item1 = ExcerptScheduleItem(
            LocalDate.now(),
            BibleExcerptAddress("Old", 1,1, 1,30),
            BibleExcerptAddress("Old", 1,1, 10,20)
        )
        val item2 = ExcerptScheduleItem(
            LocalDate.now().plusDays(1),
            BibleExcerptAddress("Old", 1,1, 4,9),
            BibleExcerptAddress("Old", 1,1, 21,41)
        )
        return ExcerptSchedule(arrayListOf(item1, item2))
    }

    fun getBibleExcerpt (address: BibleExcerptAddress) : List<String>?{
        val list = ArrayList<String>()

        var fb = false
        var fc = false
        var fv = false
        var fz = false

        try {
            val parser: XmlPullParser = getResources().getXml(R.xml.bible)
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                val TAG = "ЛогКот"
                var tmp = ""
                if (parser.eventType == XmlPullParser.START_TAG && parser.attributeCount > 0) {
                    if (parser.name == "testament") {
                        fz = parser.getAttributeValue(0) == address.testament
                    }
                    if (parser.name == "book") {
                        fb = parser.getAttributeValue(0).toInt() == address.book
                    }
                    if (parser.name == "chapter") {
                        fc = parser.getAttributeValue(0).toInt() == address.chapter
                    }
                    if (parser.name == "verse") {
                        if (parser.getAttributeValue(0).toInt() == address.startVerse) {
                            fv = true
                        }
                    }
                    if (fz && fb && fc && fv) {
                        if (parser.name == "verse") {
                            if (parser.getAttributeValue(0).toInt() <= address.endVerse) {
                                tmp = "${parser.getAttributeValue(0)} "
                                parser.next()
                                tmp += parser.text


                                list.add(tmp)
                            } else {
                                Log.d("test", list.toString())
                                return list
                            }
                        }
                    }
                }
                parser.next()
            }
        } catch (t: Throwable) {
            Toast.makeText(activity,
                "Ошибка при загрузке XML-документа: $t",
                Toast.LENGTH_LONG).show()
        }
        return null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDate = LocalDate.of(year, month, dayOfMonth)
        setCurrentExcerpt()
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

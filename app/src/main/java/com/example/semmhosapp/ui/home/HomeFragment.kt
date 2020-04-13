package com.example.semmhosapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.semmhosapp.R
import com.example.semmhosapp.utils.getDefaultSchedule
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    val shedul = getDefaultSchedule()
    val db = Firebase.firestore


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.button.setOnClickListener {
            for (item in shedul.items){
                val data = hashMapOf("date" to item.date.toString(),
                    "freeReading" to item.freeReadingExcerptAddress,
                    "groupReading" to item.groupReadingExcerptAddress)
                db.collection("Excerpts").document(item.date.toString())
                    .set(data)
                    .addOnSuccessListener {  }
                    .addOnFailureListener{  }
            }
        }
        return root
    }
}

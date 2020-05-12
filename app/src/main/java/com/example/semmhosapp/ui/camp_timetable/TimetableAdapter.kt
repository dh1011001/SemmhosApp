package com.example.semmhosapp.ui.camp_timetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.semmhosapp.R
import com.example.semmhosapp.model.Action
import com.example.semmhosapp.model.TimetableAtDay
import kotlinx.android.synthetic.main.timetable_list_item.view.*

class TimetableAdapter(val timetableAtDay: TimetableAtDay, val listener: Listener? = null) : RecyclerView.Adapter<TimetableAdapter.ActionViewHolder>() {
    interface Listener{
        fun onDeleteItemClick(item: Action)
        fun onClickItem(item: Action)
    }
    class ActionViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        lateinit var action : Action
        fun bind(action : Action) {
            this.action = action
            itemView.timeTextView.text = action.time.toString()
            itemView.actionTextView.text = action.name.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val viewHolder = ActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.timetable_list_item, parent, false))
        if (listener == null)
            viewHolder.itemView.deleteActionButton.visibility = View.GONE
        else
            viewHolder.itemView.deleteActionButton.visibility = View.VISIBLE
        return viewHolder
    }

    override fun getItemCount(): Int {
        return timetableAtDay.actions.size
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(timetableAtDay.actions[position])
        if (listener != null){
            holder.itemView.deleteActionButton.setOnClickListener {
                listener.onDeleteItemClick(timetableAtDay.actions[position])
            }
            holder.itemView.setOnClickListener {
                listener.onClickItem(timetableAtDay.actions[position])
            }
        }
    }
}
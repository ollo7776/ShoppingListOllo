package com.component.todolistollo

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.widget.LinearLayout

class RecordAdapter : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {
    private var recordsList: ArrayList<RecordModel> = ArrayList()
    private var onClickRecord: ((RecordModel) -> Unit)? = null
    private var onClickDeleteRecord: ((RecordModel) -> Unit)? = null
    private var onClickStrike: ((RecordModel) -> Unit)? = null

    fun addRecords(items: ArrayList<RecordModel>) {
        this.recordsList = items
        notifyDataSetChanged()
    }

    fun setOnClickRecord(callback: (RecordModel) -> Unit) {
        this.onClickRecord = callback
    }

    fun setOnClickDeleteRecord(callback: (RecordModel) -> Unit) {
        this.onClickDeleteRecord = callback
    }

    fun setOnClickStrike(callback: (RecordModel) -> Unit) {
        this.onClickStrike = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecordViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
    )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = recordsList[position]
        holder.bindView(record)
        holder.itemView.setOnClickListener { onClickRecord?.invoke(record) }
        holder.btnDelete.setOnClickListener { onClickDeleteRecord?.invoke(record) }
        holder.btnStrike.setOnClickListener { onClickStrike?.invoke(record) }
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var id = view.findViewById<TextView>(R.id.tvId)
        private var title = view.findViewById<TextView>(R.id.tvTitle)
        //private var marked = view.findViewById<TextView>(R.id.tvMarked)
//      private var strike = view.findViewById<TextView>(R.id.tvStrike)
        var btnDelete: Button = view.findViewById(R.id.btnDelete)
        var btnStrike: Button = view.findViewById(R.id.btnStrike)
        private var recordItem = view.findViewById<LinearLayout>(R.id.recordItem)

        fun bindView(record: RecordModel) {
          //  id.text = record.id.toString()
            title.text = record.title
          //  marked.text = record.recordMarked.toString()
            var strike = record.strikeTrough
            var marked = record.recordMarked

            if (strike == 1) {
                title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
            } else if (strike == 0) {
                title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }
            if(marked == 1) {
                recordItem.setBackgroundColor(Color.parseColor("#D6D6D6"))
            } else if(marked == 0) {
                recordItem.setBackgroundColor(Color.parseColor("#ECECEC"))
            }
        }
    }
}

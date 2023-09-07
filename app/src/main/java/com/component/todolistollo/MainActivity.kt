package com.component.todolistollo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import layout.SQLiteHelper


class MainActivity : AppCompatActivity() {
    private lateinit var edTitle: EditText
    private lateinit var btnAdd: Button

    //    private lateinit var btnView: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDeleteAll: Button
    private lateinit var btnUp: Button
    private lateinit var btnDown: Button
    private lateinit var btnClear: Button

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: RecordAdapter? = null
    private var record: RecordModel? = null
 //   val records2Obj: ArrayList<RecordModel> = ArrayList()

    override fun onCreate(savedInstantState: Bundle?) {
        super.onCreate(savedInstantState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initView()
        initRecyclerView()

        sqLiteHelper = SQLiteHelper(this)
        btnAdd.setOnClickListener { addRecord() }
//      btnView.setOnClickListener { getRecords() }
        btnUpdate.setOnClickListener { updateRecord() }
        btnDeleteAll.setOnClickListener { delAllStrikedRecords() }
        btnUp.setOnClickListener { setRecordUp() }
        btnDown.setOnClickListener { setRecordDown() }
        btnClear.setOnClickListener { clearEditText() }

        adapter?.setOnClickRecord {
            edTitle.setText(it.title)
            record = it
// Saving array of two objects - the last two clicked records on the list
//            val recordObj = RecordModel(
//                id = record!!.id,
//                title = record!!.title,
//                strikeTrough = record!!.strikeTrough,
//                recordMarked = record!!.recordMarked,
//            )
            val markedFromClickedRec = sqLiteHelper.checkMarkedInRecord(it.id)
            if (markedFromClickedRec == 1) {
                unmarkOneRecord(it.id)
            } else {
                sqLiteHelper.unmarkAll()
                markClickedRecord(it.id)
            }
            // Toast.makeText(this, "it" + it.recordMarked, Toast.LENGTH_LONG).show()
            getRecords()
        }
        adapter?.setOnClickDeleteRecord { deleteRecord(it.id) }
        adapter?.setOnClickStrike { strikeTrough(it.id) }
//        adapter?.setOnClickUp { setRecordUp(it.id) }
//        adapter?.setOnClickDown { setRecordDown(it.id) }
        getRecords()
    }

    fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordAdapter()
        recyclerView.adapter = adapter
    }

    private fun addRecord() {
        val title = edTitle.text.toString()
        if (title.isEmpty()) {
//            Toast.makeText(this, "Bitte füllen Sie das erforderliche Feld aus", Toast.LENGTH_SHORT)
            Toast.makeText(this, R.string.fill_required_field, Toast.LENGTH_SHORT)
                .show()
        } else {
            val record = RecordModel(title = title)
            val status = sqLiteHelper.insertRecord(record)
            if (status > -1) {
//                Toast.makeText(this, "Die Daten wurden gespeichert", Toast.LENGTH_SHORT).show()
                clearEditText()
                getRecords()
            } else {
//                Toast.makeText(this, "Die Daten wurden nicht gespeichert", Toast.LENGTH_SHORT)
                Toast.makeText(this, R.string.data_not_saved, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateRecord() {
        val titleInput = edTitle.text.toString()
        val updatedRecord = RecordModel(id = record!!.id, title = titleInput)

        if (title == "") {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.add_empty_title)
            builder.setCancelable(true)
            builder.setPositiveButton(R.string.yes) { dialog, _ ->
                sqLiteHelper.updateRecord(updatedRecord)
                getRecords()
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
            return
        }

        if (record == null) {
            Toast.makeText(this, "Record null", Toast.LENGTH_SHORT).show()
            return
        }

        val status = sqLiteHelper.updateRecord(updatedRecord)
        if (status > -1) {
            clearEditText()
            getRecords()
          //  Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(this, "Update fehlgeschlagen", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun strikeTrough(id: Int) {
        if (id == null) return
        val status = sqLiteHelper.strikeRecord(id)
        if (status > -1) {
            clearEditText()
            getRecords()
        } else {
            Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun markClickedRecord(id: Int) {
        if (id == null) return
        val status = sqLiteHelper.markClickedRecord(id)
        if (status > -1) {
            getRecords()
        } else {
            Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun unmarkOneRecord(id: Int) {
        if (id == null) return
        val status = sqLiteHelper.unmarkOneRecord(id)
        if (status > -1) {
            getRecords()
        } else {
            Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRecord(id: Int) {
        if (id == null) return
        val builder = AlertDialog.Builder(this)
//        builder.setMessage("Möchten Sie dieses Element wirklich löschen?")
        builder.setMessage(R.string.delete_this_item)
        builder.setCancelable(true)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            sqLiteHelper.deleteRecord(id)
            getRecords()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun delAllStrikedRecords() {
        val builder = AlertDialog.Builder(this)
//        builder.setMessage("Möchten Sie alle durchgestrichenen Elemente löschen?")
        builder.setMessage(R.string.crossed_out_delete)
        builder.setCancelable(true)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            sqLiteHelper.deleteAllStrikedRecords()
            getRecords()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun setRecordUp() {
        sqLiteHelper.pushUpRecord("up")
        getRecords()
    }

    private fun setRecordDown() {
        sqLiteHelper.pushUpRecord("down")
        getRecords()
    }

    private fun clearEditText() {
        edTitle.setText("")
        edTitle.requestFocus()
    }

    fun initView() {
        edTitle = findViewById(R.id.edTitle)
        btnAdd = findViewById(R.id.btnAdd)
//        btnView = findViewById(R.id.btnView)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDeleteAll = findViewById(R.id.btnDeleteAll)
        btnUp = findViewById(R.id.btnUp)
        btnDown = findViewById(R.id.btnDown)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun getRecords() {
        val recordsList = sqLiteHelper.getAllRecords()
        adapter?.addRecords(recordsList)
    }

}
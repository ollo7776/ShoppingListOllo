package layout

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.component.todolistollo.RecordModel

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "records6.db"
        private const val TBL_RECORDS = "tbl_records"
        private const val ID = "id"
        private const val TITLE = "title"
        private const val STRIKETHROUGH = "strike"
        private const val RECORDMARKED = "marked"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblRecords = ("CREATE TABLE " + TBL_RECORDS + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT,"
                + STRIKETHROUGH + " INTEGER,"
                + RECORDMARKED + " INTEGER"
                + ")")
        db?.execSQL(createTblRecords)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_RECORDS")
        onCreate(db)
    }

    fun insertRecord(record: RecordModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
//        contentValues.put(ID, record.id) // Do not uncomment it because of autoincrement
        contentValues.put(TITLE, record.title)
        contentValues.put(STRIKETHROUGH, record.strikeTrough)
        contentValues.put(RECORDMARKED, record.recordMarked)

        val success = db.insert(TBL_RECORDS, null, contentValues)
        db.close()
        return success
    }

    fun getAllRecords(): ArrayList<RecordModel> {
        val records: ArrayList<RecordModel> = ArrayList()
//        val selectQuery = "SELECT * FROM $TBL_RECORDS"
        val selectQuery = "SELECT * FROM $TBL_RECORDS ORDER BY ${ID} ASC"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var title: String
        var strikeTrough: Int
        var recordMarked: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                strikeTrough = cursor.getInt(cursor.getColumnIndexOrThrow("strike"))
                recordMarked = cursor.getInt(cursor.getColumnIndexOrThrow("marked"))
                val record = RecordModel(
                    id = id,
                    title = title,
                    strikeTrough = strikeTrough,
                    recordMarked = recordMarked
                )
                records.add(record)
            } while (cursor.moveToNext())
        }
        return records
    }

    fun updateRecord(record: RecordModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, record.id)
        contentValues.put(TITLE, record.title)
        contentValues.put(STRIKETHROUGH, record.strikeTrough)
        contentValues.put(RECORDMARKED, record.recordMarked)

        val success = db.update(TBL_RECORDS, contentValues, "id=" + record.id, null)
        db.close()
        return success
    }

    fun strikeRecord(id: Int): Int {
        var records = getAllRecords()
        var oldRecord = records.find { it.id == id }
        var oldStrike = oldRecord?.strikeTrough.toString()
        var strike: Int
        if (oldStrike == "0") strike = 1 else strike = 0
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)
        contentValues.put(STRIKETHROUGH, strike)
        val success = db.update(TBL_RECORDS, contentValues, "id=" + id, null)
        db.close()
        return success
    }

    fun checkMarkedInRecord(id: Int): Int {
        var records = getAllRecords()
        var record = records.find { it.id == id }
        return record!!.recordMarked
    }

    fun markClickedRecord(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)
        contentValues.put(RECORDMARKED, 1)
        val success = db.update(TBL_RECORDS, contentValues, "id=" + id, null)
        db.close()
        return success
    }

    fun unmarkOneRecord(id: Int): Int {
        var records = getAllRecords()
        // var oldRecord = records.find { it.id == id }
        //  var oldMarked = oldRecord?.recordMarked.toString()
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)
        contentValues.put(RECORDMARKED, 0)
        val success = db.update(TBL_RECORDS, contentValues, "id=" + id, null)
        db.close()
        return success
    }

    fun unmarkAll(): Int {
        var records = getAllRecords()
        val db = this.writableDatabase
        var success = 1
        for (item in records) {
            val contentValues = ContentValues()
            contentValues.put(ID, item.id)
            contentValues.put(RECORDMARKED, 0)
            success = db.update(TBL_RECORDS, contentValues, "id=${item.id}", null)
        }
        db.close()
        return success
    }


    fun deleteRecord(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TBL_RECORDS, "id=$id", null)
        db.close()
        return success
    }

    fun deleteAllStrikedRecords(): Int {
        val db = this.writableDatabase
        val success = db.delete(TBL_RECORDS, "strike= 1", null)
        db.close()
        return success
    }

    fun pushUpRecord(direction: String) {
        var recordsFromDb = getAllRecords()
        for (i in recordsFromDb.indices) {
            if (recordsFromDb[i].recordMarked == 1) {
                var tempRecordA = recordsFromDb[i]
                val tempId = tempRecordA.id
                if (direction == "up" && i > 0) {
                    var tempRecordB = recordsFromDb[i - 1]
                    tempRecordA.id = tempRecordB.id
                    tempRecordB.id = tempId
                    updateRecord(tempRecordA)
                    updateRecord(tempRecordB)
                } else if (direction == "down" && i < recordsFromDb.size -1) {
                    var tempRecordB = recordsFromDb[i + 1]
                    tempRecordA.id = tempRecordB.id
                    tempRecordB.id = tempId
                    updateRecord(tempRecordA)
                    updateRecord(tempRecordB)
                }
            }
        }
    }

    //    fun replaceRecords(records: ArrayList<RecordModel>): ArrayList<RecordModel> {
    fun replaceRecords(records: ArrayList<RecordModel>, direction: String) {
        var recordsFromDB: ArrayList<RecordModel> = getAllRecords()

        for (rec in recordsFromDB) {
            deleteRecord(rec.id)
            if (rec.id == records[0].id) {
                continue
            }
            if (rec.id == records[1].id) {
                if (direction.equals("down")) {
                    insertRecord(rec)
                    insertRecord(records[0])
                } else if (direction.equals("up")) {
                    insertRecord(records[0])
                    insertRecord(rec)
                }
                continue
            }
            insertRecord(rec)
        }
    }
}
package app.kcdemos.com.sqllitedemo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

private const val DB_NAME = "my_todo_db"

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {

    val ToDoItemsList: List<ToDoItemModel>
        get() {
            val todoItems = ArrayList<ToDoItemModel>()
            val selectQuery = "SELECT  * FROM " + ToDoItemModel.TODO_TABLE_NAME + " ORDER BY " +
                    ToDoItemModel.TODO_TIMESTAMP_COLUMN + " DESC"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val todoItem = ToDoItemModel()
                    todoItem.todoId = cursor.getInt(cursor.getColumnIndex(ToDoItemModel.TODO_ID_COLUMN))
                    todoItem.todoTitle = cursor.getString(cursor.getColumnIndex(ToDoItemModel.TODO_TITLE_COLUMN))
                    todoItem.todoTimestamp = cursor.getString(cursor.getColumnIndex(ToDoItemModel.TODO_TIMESTAMP_COLUMN))

                    todoItems.add(todoItem)
                } while (cursor.moveToNext())
            }
            db.close()
            return todoItems
        }

    // return count
    val itemsCount: Int
        get() {
            val countQuery = "SELECT  * FROM " + ToDoItemModel.TODO_TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)

            val count = cursor.count
            cursor.close()
            return count
        }

    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ToDoItemModel.CREATE_TABLE)
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ToDoItemModel.TODO_TABLE_NAME)

        // Create tables again
        onCreate(db)
    }

    fun insertToDoItem(todoItem: String): Long {
        // get writable database as we want to write data
        val db = this.writableDatabase

        val values = ContentValues()
        // `id` and `todoEntryTime` are auto incremental - auto insert values
        values.put(ToDoItemModel.TODO_TITLE_COLUMN, todoItem)

        val id = db.insert(ToDoItemModel.TODO_TABLE_NAME, null, values)

        // close connection - important
        db.close()

        // return new id
        return id
    }

    fun getToDoItem(id: Long): ToDoItemModel {
        // get readable database as we are not inserting anything
        val db = this.readableDatabase

        val cursor = db.query(ToDoItemModel.TODO_TABLE_NAME,
                arrayOf(ToDoItemModel.TODO_ID_COLUMN, ToDoItemModel.TODO_TITLE_COLUMN, ToDoItemModel.TODO_TIMESTAMP_COLUMN),
                ToDoItemModel.TODO_ID_COLUMN + "=?",
                arrayOf(id.toString()), null, null, null, null)

        cursor?.moveToFirst()

        // prepare dialog_entry object
        val todoItem = ToDoItemModel(
                cursor!!.getInt(cursor.getColumnIndex(ToDoItemModel.TODO_ID_COLUMN)),
                cursor.getString(cursor.getColumnIndex(ToDoItemModel.TODO_TITLE_COLUMN)),
                cursor.getString(cursor.getColumnIndex(ToDoItemModel.TODO_TIMESTAMP_COLUMN)))

        // close the db connection
        cursor.close()

        return todoItem
    }

    fun updateToDoItem(toDoItemModel: ToDoItemModel): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(ToDoItemModel.TODO_TITLE_COLUMN, toDoItemModel.todoTitle)

        // updating row
        return db.update(ToDoItemModel.TODO_TABLE_NAME, values, ToDoItemModel.TODO_ID_COLUMN + " = ?",
                arrayOf(toDoItemModel.todoId.toString()))
    }

    fun deleteToDoItem(toDoItemModel: ToDoItemModel) {
        val db = this.writableDatabase
        db.delete(ToDoItemModel.TODO_TABLE_NAME, ToDoItemModel.TODO_ID_COLUMN + " = ?",
                arrayOf(toDoItemModel.todoId.toString()))
        db.close()
    }

    fun deleteAllToDoItems() {
        val db = this.writableDatabase
        db.delete(ToDoItemModel.TODO_TABLE_NAME, ToDoItemModel.TODO_ID_COLUMN + " > ?",
                arrayOf("0"))
        db.close()
    }

}
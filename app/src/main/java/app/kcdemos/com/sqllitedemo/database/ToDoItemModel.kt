package app.kcdemos.com.sqllitedemo.database

class ToDoItemModel {

    var todoId: Int = 0
    var todoTitle: String? = null
    var todoTimestamp: String? = null

    constructor()

    constructor(todo_id: Int, todo_title: String, todo_timestamp: String) {
        this.todoId = todo_id
        this.todoTitle = todo_title
        this.todoTimestamp = todo_timestamp
    }

    companion object {
        const val TODO_TABLE_NAME = "todo_db"
        const val TODO_ID_COLUMN = "id"
        const val TODO_TITLE_COLUMN = "todoTitle"
        const val TODO_TIMESTAMP_COLUMN = "todoTimeStamp"

        const val CREATE_TABLE = (
                "CREATE TABLE "
                + TODO_TABLE_NAME + "("
                + TODO_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TODO_TITLE_COLUMN + " TEXT,"
                + TODO_TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")")
    }
}
package app.kcdemos.com.sqllitedemo

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import app.kcdemos.com.sqllitedemo.database.DBHelper
import app.kcdemos.com.sqllitedemo.database.ToDoItemModel
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mAdapter: ToDoItemAdapter? = null
    private var todoItemsList = ArrayList<ToDoItemModel>()
    private var coordinatorLayout: CoordinatorLayout? = null
    private var recyclerView: RecyclerView? = null

    private var db: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(Color.BLACK)
        setSupportActionBar(toolbar)

        setupControls()
    }

    private fun setupControls() {
        coordinatorLayout = findViewById(R.id.outer_main)
        recyclerView = findViewById(R.id.recycler_main)

        db = DBHelper(this)

        todoItemsList.addAll(db!!.ToDoItemsList)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { showEntryDialog(false, null, -1) }

        mAdapter = ToDoItemAdapter(this, todoItemsList)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter


        recyclerView!!.addOnItemTouchListener(ItemLongPressListener(this,
                recyclerView!!, object : ItemLongPressListener.ClickListener {
            override fun onClick(view: View, position: Int) {}

            override fun onLongClick(view: View?, position: Int) {
                showActionsDialog(position)
            }
        }))
    }

    private fun createTodoItem(todoTitle: String) {
        val id = db!!.insertToDoItem(todoTitle)
        val n = db!!.getToDoItem(id)

        if (n != null) {
            todoItemsList.add(0, n)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    private fun updateToDoItem(todoTitle: String, position: Int) {
        val n = todoItemsList[position]
        n.todoTitle = (todoTitle)
        db!!.updateToDoItem(n)

        // refreshing items
        todoItemsList[position] = n
        mAdapter!!.notifyItemChanged(position)

    }

    private fun deleteToDoItem(position: Int) {
        // deleting the item from db
        db!!.deleteToDoItem(todoItemsList[position])

        // refreshing items
        todoItemsList.removeAt(position)
        mAdapter!!.notifyItemRemoved(position)

    }

    private fun deleteAllToDoItems() {
        // deleting all the items from db
        db!!.deleteAllToDoItems()
        todoItemsList.removeAll(todoItemsList)
        mAdapter!!.notifyDataSetChanged()

    }

    private fun showActionsDialog(position: Int) {
        val options = arrayOf<CharSequence>("Editar esta Tarefa", "Deletar esta Tarefa?", "Deletar TODAS as Tarefas?")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Action")
        builder.setItems(options) { dialog, itemIndex ->
            // add more options check index
            when (itemIndex) {
                0 -> showEntryDialog(true, todoItemsList[position], position)
                1 -> deleteToDoItem(position)
                2 -> deleteAllToDoItems()
                else -> Toast.makeText(applicationContext, "never gonna load", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    private fun showEntryDialog(shouldUpdate: Boolean, toDoItemModel: ToDoItemModel?, position: Int) {

        val layoutInflaterAndroid = LayoutInflater.from(applicationContext)
        val view = layoutInflaterAndroid.inflate(R.layout.todo_entry_dialog, null)

        val alertDialogBuilderUserInput = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilderUserInput.setView(view)

        val todoItem = view.findViewById<EditText>(R.id.dialog_entry)
        val dialogTitle = view.findViewById<TextView>(R.id.dialog_header)
        dialogTitle.text = if (!shouldUpdate) getString(R.string.dialog_new_entry_title) else getString(R.string.dialog_edit_entry_title)

        if (shouldUpdate && toDoItemModel != null) {
            todoItem.setText(toDoItemModel.todoTitle)
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(if (shouldUpdate) "Atualizar" else "Salvar") { dialogBox, id -> }
                .setNegativeButton("Cancelar"
                ) { dialogBox, id -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(todoItem.text.toString())) {
                Toast.makeText(this@MainActivity, "Tarefa incluida com Sucesso", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                alertDialog.dismiss()
            }

            // check if user updating dialog_entry
            if (shouldUpdate && toDoItemModel != null) {
                // update dialog_entry by it's id
                updateToDoItem(todoItem.text.toString(), position)
            } else {
                // create new dialog_entry
                createTodoItem(todoItem.text.toString())
            }
        })
    }

}
package app.kcdemos.com.sqllitedemo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.kcdemos.com.sqllitedemo.database.ToDoItemModel
import java.text.ParseException
import java.text.SimpleDateFormat


class ToDoItemAdapter(private val context: Context, private val todoItemsList: List<ToDoItemModel>) : RecyclerView.Adapter<ToDoItemAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var todoEntry: TextView = view.findViewById(R.id.todo_entry)
        var todoEntryTime: TextView = view.findViewById(R.id.todo_timestamp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todoItem = todoItemsList[position]

        holder.todoEntry.setText(todoItem.todoTitle)
        holder.todoEntryTime.text = showShortDate(todoItem.todoTimestamp!!)
    }

    override fun getItemCount(): Int {
        return todoItemsList.size
    }

    private fun showShortDate(dateStr: String): String {
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = fmt.parse(dateStr)
            val fmtOut = SimpleDateFormat("MMM d, yyyy")
            return fmtOut.format(date)
        } catch (e: ParseException) {
            //We can log this exception in real app
            return ""
        }
    }
}
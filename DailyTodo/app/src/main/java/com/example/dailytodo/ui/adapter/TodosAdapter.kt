package com.example.dailytodo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytodo.R
import com.example.dailytodo.data.model.TodoModel
import com.example.dailytodo.ui.uiInterface.DeleteAndUpdateButtonSelection
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_daily_todo.*

class TodosAdapter(var deleteAndUpdateButtonSelection: DeleteAndUpdateButtonSelection) : ListAdapter<TodoModel, TodosAdapter.TodosHolder>(DiffUtilMovesItemList()) {


    class TodosHolder (itemView: View) : RecyclerView.ViewHolder(itemView),
        LayoutContainer {
        override val containerView: View?
            get() = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodosHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return TodosHolder(
            layoutInflater.inflate(
                R.layout.item_daily_todo,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodosHolder, position: Int) {
        holder.tvTitle.text = getItem(position).title
        holder.tvDetails.text = getItem(position).details
        holder.tvDate.text = getItem(position).date

        holder.ivEdit.setOnClickListener {
            deleteAndUpdateButtonSelection.updateTodo(getItem(position),position)
        }
        holder.ivDelete.setOnClickListener {
            deleteAndUpdateButtonSelection.deleteTodo(getItem(position))
        }
    }

    class DiffUtilMovesItemList : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(
            oldItem: TodoModel,
            newItem: TodoModel
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: TodoModel,
            newItem: TodoModel
        ): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

    }

}
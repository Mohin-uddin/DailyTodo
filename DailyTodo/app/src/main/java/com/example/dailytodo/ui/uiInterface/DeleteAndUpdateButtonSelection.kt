package com.example.dailytodo.ui.uiInterface

import com.example.dailytodo.data.model.TodoModel
import java.text.FieldPosition

interface DeleteAndUpdateButtonSelection {
    fun updateTodo(todoModel: TodoModel,position: Int)
    fun deleteTodo(todoModel: TodoModel)
}
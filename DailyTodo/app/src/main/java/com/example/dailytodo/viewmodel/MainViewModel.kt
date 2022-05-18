package com.example.dailytodo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dailytodo.data.model.SuccessResponse
import com.example.dailytodo.data.model.TodoModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(val reference: DatabaseReference) : ViewModel() {

    private var _isLoaderState = MutableStateFlow<SuccessResponse?>(null)
    var isLoaderState: StateFlow<SuccessResponse?> = _isLoaderState

    private var _todoList = MutableStateFlow<List<TodoModel>?>(null)
    var todoList: StateFlow<List<TodoModel>?> = _todoList

    fun getTodoList(userId:String) {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("checakdata", "onDataChange: " + snapshot.toString())

                try {
                    var listTodo = ArrayList<TodoModel>()
                    _todoList.value=null
                    for (data in snapshot.children) {
                        var dataModel = data.getValue(TodoModel::class.java)
                        if (dataModel?.userId==userId) {
                            listTodo.add(dataModel)
                        }
                    }
                    Log.e("checakdata", "onDataChange: " + listTodo.size)
                    if (listTodo!=null)
                        _todoList.value = listTodo
                } catch (error: Exception) {
                    Log.e("checakdata", "onDataChange: " + error)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("checakdata", "onDataChange: " + error)
            }

        })
    }

    fun searchData(searchData:String){
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("checakdata", "onDataChange: " + snapshot.toString())

                try {
                    var listTodo = ArrayList<TodoModel>()
                    _todoList.value=null
                    for (data in snapshot.children) {
                        var dataModel = data.getValue(TodoModel::class.java)
                        if (dataModel!!.title!!.contains(searchData)) {
                            listTodo.add(dataModel)
                        }
                    }
                    Log.e("checakdata", "onDataChange: " + listTodo.size)
                    if (listTodo!=null)
                        _todoList.value = listTodo
                } catch (error: Exception) {
                    Log.e("checakdata", "onDataChange: " + error)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("checakdata", "onDataChange: " + error)
            }

        })
    }
    fun updateTodo(todoModel: TodoModel) {
        var hasmapData = HashMap<String, Any>()
        hasmapData["uiD"] = todoModel.uiD ?: ""
        hasmapData["title"] = todoModel.title.toString()
        hasmapData["details"] = todoModel.details.toString()
        hasmapData["data"] = todoModel.date.toString()
        reference.child(todoModel.uiD.toString()).updateChildren(hasmapData)
            .addOnSuccessListener {
                var response = SuccessResponse(true, "Successfully update"+it)
                _isLoaderState.value = response
            }
            .addOnFailureListener {
                var response = SuccessResponse(true, it.message.toString())
                _isLoaderState.value = response
            }
    }

    fun deleteTodo(todoModel: TodoModel) {
        reference.child(todoModel.uiD.toString()).removeValue().addOnSuccessListener {
            var response = SuccessResponse(true, "Successfully Delete")
            _isLoaderState.value = response
        }
            .addOnFailureListener {
                var response = SuccessResponse(true, it.message.toString())
                _isLoaderState.value = response
            }
    }


}
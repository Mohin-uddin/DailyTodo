package com.example.dailytodo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.dailytodo.data.model.SuccessResponse
import com.example.dailytodo.data.model.TodoModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(val reference: DatabaseReference) : ViewModel() {

    private var _isLoaderState = MutableStateFlow<SuccessResponse?>(null)
    var isLoaderState: StateFlow<SuccessResponse?> = _isLoaderState

    private var _todoList = MutableStateFlow<List<TodoModel>?>(null)
    var dailyTodoList: StateFlow<List<TodoModel>?> = _todoList

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

    fun searchData(searchData:String,userID: String){

        val taksList = ArrayList<TodoModel>()
        var database = FirebaseFirestore.getInstance()
        database
            .collection("User")
            .document(userID)
            .collection("Todos")
            .get()
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) { //Here should be task1, not task

                    for (snapshot in Objects.requireNonNull(task1.result)) {
                        Log.e("DataChecking", "Error: "+snapshot.toString())
                        val todo: TodoModel =
                            snapshot.toObject(TodoModel::class.java)
                        if (todo!!.title!!.contains(searchData)) {
                            taksList.add(todo)
                        }

                    }
                    _todoList.value=taksList
                }
                else
                {

                }
            }
            .addOnFailureListener { e ->

            }
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

    fun fireUpdateTodo(todoModel: TodoModel){
        val todos= Firebase.firestore.collection("User").document(todoModel.userId!!).collection("Todos")

        todos.document(todoModel.uiD!!).set(todoModel, SetOptions.merge()).addOnSuccessListener {
            Log.e("CheckData", "fireUpdateTodo: " )
            var response = SuccessResponse(true, "Successfully update")
            _isLoaderState.value = response
        }

    }
    fun deleteTodo(todoModel: TodoModel) {
        val todos= Firebase.firestore.collection("User").document(todoModel.userId!!).collection("Todos")

        todos.document(todoModel.uiD!!).delete().addOnSuccessListener {
            Log.e("CheckData", "fireUpdateTodo: delete " )
            var response = SuccessResponse(true, "Successfully Delete")
            _isLoaderState.value = response

        }.addOnFailureListener {
            var response = SuccessResponse(true, it.message.toString())
            _isLoaderState.value = response
        }
    }


     fun userAllTodos(userID: String) {
        val taksList = ArrayList<TodoModel>()
        var database = FirebaseFirestore.getInstance()
        database
            .collection("User")
            .document(userID)
            .collection("Todos")
            .get()
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) { //Here should be task1, not task
                    Log.e("CheckData", "updateTodo: data update")
                    for (snapshot in Objects.requireNonNull(task1.result)) {
                        Log.e("DataChecking", "Error: "+snapshot.toString())
                        val product: TodoModel =
                            snapshot.toObject(TodoModel::class.java)
                        taksList.add(product)
                    }
                    if (taksList!=null) {
                        _todoList.value = taksList
                        Log.e(
                            "CheckData",
                            "updateTodo: data update" + dailyTodoList.value.toString()
                        )
                    }
                }
                else
                {

                }
            }
            .addOnFailureListener { e ->

            }
    }
}
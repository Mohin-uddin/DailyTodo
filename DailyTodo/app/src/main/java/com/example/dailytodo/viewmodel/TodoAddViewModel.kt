package com.example.dailytodo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dailytodo.data.model.SuccessResponse
import com.example.dailytodo.data.model.TodoModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TodoAddViewModel @Inject constructor(val reference:DatabaseReference) : ViewModel() {
    private var _isLoaderState = MutableStateFlow<SuccessResponse?>(null)
    var isLoaderState: StateFlow<SuccessResponse?> = _isLoaderState
    fun setData(todoModel: TodoModel){

        val id = reference.push().key
        Log.e("Datasave", "setData: "+id )
        todoModel.uiD= id!!
        reference.child(id).setValue(todoModel).addOnCompleteListener {
            if(it.isSuccessful){
               val  loaderState = SuccessResponse(true,"Successfully save data")
                _isLoaderState.value = loaderState
            }
            else
            {
                val  loaderState = SuccessResponse(true,it.exception.toString())
                _isLoaderState.value = loaderState
            }
        }
    }
}
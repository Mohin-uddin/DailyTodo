package com.example.dailytodo.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dailytodo.R
import com.example.dailytodo.data.model.TodoModel
import com.example.dailytodo.utils.ConstValue
import com.example.dailytodo.viewmodel.TodoAddViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orhanobut.dialogplus.DialogPlus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddTodoActivity : AppCompatActivity() {
    var cal = Calendar.getInstance()
    lateinit var dialog : Dialog
    private val viewModel:TodoAddViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)
        val preference = getSharedPreferences("Todo", Context.MODE_PRIVATE)
        val userId= preference?.getString("userID","")?:"false"
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        tvDatePicker.setOnClickListener {
            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        tvAddTodo.setOnClickListener {
             dialog = ConstValue.animation(this,0)
            val todos= Firebase.firestore.collection("User").document(userId).collection("Todos")
            val key = todos.document().id
            val todoAddModel = TodoModel(key,etNoteTitle.text.toString(),etNoteDetails.text.toString()
                ,tvDatePicker.text.toString(),userId)
            lifecycleScope.launch {
                todos.document(key).set(todoAddModel).addOnSuccessListener {
                    dialog.dismiss()
                    onBackPressed()
                }
            }

        }

        lifecycleScope.launch {
            viewModel.isLoaderState.collect {
                if (it!=null){
                    if(viewModel.isLoaderState.value!!.state) {
                        dialog.dismiss()
                        Toast.makeText(this@AddTodoActivity, it.message, Toast.LENGTH_LONG).show()
                        viewModel.isLoaderState.value?.state = false
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tvDatePicker.text = sdf.format(cal.time)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
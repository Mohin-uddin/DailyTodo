package com.example.dailytodo.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytodo.R
import com.example.dailytodo.data.model.TodoModel
import com.example.dailytodo.ui.adapter.TodosAdapter
import com.example.dailytodo.ui.uiInterface.DeleteAndUpdateButtonSelection
import com.example.dailytodo.utils.ConstValue
import com.example.dailytodo.viewmodel.MainViewModel
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.android.synthetic.main.activity_add_todo.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_daily_todo.view.*
import kotlinx.android.synthetic.main.toolbar_icon_with_menu.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DeleteAndUpdateButtonSelection{
    lateinit var todoAdapter: TodosAdapter
    var cal = Calendar.getInstance()
    private lateinit var dialougeTodo: Dialog

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val preference = getSharedPreferences("Todo", Context.MODE_PRIVATE)
        val userId= preference?.getString("userID","")?:"false"
        viewModel.getTodoList(userId)
        dialougeTodo= ConstValue.animation(this,0)
        adapterInitialize()
        lifecycleScope.launch {
            viewModel.todoList.collect {
                if (it!=null){
                    Log.e("checakdata", "onCreate: " )
                    dialougeTodo.dismiss()
                    todoAdapter.submitList(it)
                }
            }
        }

        ivAddTodo.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java))
        }

        ivLogout.setOnClickListener {
            val preference = getSharedPreferences("Todo", Context.MODE_PRIVATE)
            val editor = preference.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.putString("userID", "it.userId")
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        svSearchTodo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchData(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchData(newText)
                return false
            }
        })
    }


    private fun adapterInitialize() {
        todoAdapter = TodosAdapter(this)
        rvTodoList.layoutManager = LinearLayoutManager(this@MainActivity)
        rvTodoList.adapter = todoAdapter
    }


    private fun datasetView(viewRef: View, todoModel: TodoModel) {
        viewRef.etNoteTitle.setText(todoModel.title)
        viewRef.etNoteDetails.setText(todoModel.details)
        viewRef.tvDatePicker.text = todoModel.date

        viewRef.tvDatePicker.setOnClickListener {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    viewRef.tvDatePicker.text=updateDateInView()
                }
            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    override fun updateTodo(todoModel: TodoModel, position: Int) {

        val dialog = DialogPlus.newDialog(this)
            .setContentHolder(ViewHolder(R.layout.activity_add_todo))
            .setExpanded(true) // This will enable the expand feature, (similar to android L share dialog)
            .create()

        val view: View = dialog.holderView
        view.tvAddTodo.text="Update"
        datasetView(view,todoModel)
        view.tvAddTodo.setOnClickListener {
            todoModel.title = view.etNoteTitle.text.toString()
            todoModel.details = view.etNoteDetails.text.toString()
            todoModel.date = view.tvDatePicker.text.toString()
            viewModel.updateTodo(todoModel)
        }


        dialog.show()

        lifecycleScope.launch {
            viewModel.isLoaderState.collect {
                if (it!=null){
                    if(viewModel.isLoaderState.value!!.state) {
                        dialog.dismiss()
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                        viewModel.isLoaderState.value?.state = false
                      //  viewModel.getTodoList()
                    }
                }
            }
        }

    }

    override fun deleteTodo(todoModel: TodoModel) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.exit_application))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes),
                DialogInterface.OnClickListener {
                        dialog, id ->
                    viewModel.deleteTodo(todoModel)
                })
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun updateDateInView(): String {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }

}
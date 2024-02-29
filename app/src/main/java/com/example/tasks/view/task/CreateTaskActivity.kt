package com.example.tasks.view.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.tasks.R
import com.example.tasks.databinding.ActivityCreateTaskBinding
import com.example.tasks.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCreateTaskBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val taskViewModel: TaskViewModel by viewModels()

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val REQ_EDIT = "req_edit"
        private var isEdit = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra(REQ_EDIT, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        setupToolbar()
        createTask()
        updateView()
    }

    fun updateView(){
        if (isEdit) {
            var task = intent.getSerializableExtra(EXTRA_DATA) as Task
            binding.etTitle.setText(task.title.toString())
            binding.etDescription.setText(task.description.toString())
        }
    }

    private fun createTask() {
        binding.btnSaveTask.setOnClickListener {
            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
            if(intent.getSerializableExtra(EXTRA_DATA) != null){
                var task = intent.getSerializableExtra(EXTRA_DATA) as Task

                val taskUpdate = Task(
                    task.id,
                    currentUserID,
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                )
                taskViewModel.updateTask(taskUpdate)
                finish()
            }else{
                var task = Task()
                val newTask = Task(
                    task.id,
                    currentUserID,
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                )
                taskViewModel.createTask(newTask)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        title = ""
        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }
}
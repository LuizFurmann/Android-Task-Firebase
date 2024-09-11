package com.example.tasks.view.task

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.tasks.R
import com.example.tasks.databinding.ActivityTaskDetailsBinding
import com.example.tasks.model.Task

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTaskDetailsBinding
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.getSerializableExtra("Task") != null) {
            task = intent.getSerializableExtra("Task") as Task
            updateView(task)
        }
    }

    private fun updateView(task: Task){
        if (task.image != null) {
            var myUri: Uri = Uri.parse(task.image);
            binding.imgTask.setImageURI(myUri)
        }

        binding.tvTitle.text = task.title
        binding.tvDescription.text = task.description

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
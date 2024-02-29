package com.example.tasks.view.task

import android.content.Intent
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
            setupToolbar()
            updateView(task)
        }
    }

    private fun updateView(task: Task){
        binding.tvTitle.text = task.title
        binding.tvDescription.text = task.description
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
        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }
}
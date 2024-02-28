package com.example.tasks.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasks.R
import com.example.tasks.databinding.ActivityMainBinding
import com.example.tasks.model.Task
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var taskAdapter = TaskAdapter(this)
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        createNewTask()
    }

    private fun setupViewModel(){
        taskViewModel.getTasks().observe(this) { task ->
            lifecycleScope.launch {
                updateList(task)
            }
        }
    }

    private fun setupRecyclerView(){
        binding.rvTasks.adapter = taskAdapter
        binding.rvTasks.layoutManager = GridLayoutManager(this, 2)
    }

    private fun updateList(trainings: List<Task>){
        if (trainings.isEmpty()) {
            binding.rvTasks.visibility = View.GONE
//            binding.tilEmptyList.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
//            binding.tilEmptyList.visibility = View.GONE
            taskAdapter.updateList(trainings)
        }
    }

    private fun createNewTask(){
        binding.fabNewTask.setOnClickListener {
            Intent(this@MainActivity, TaskDetailsActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}
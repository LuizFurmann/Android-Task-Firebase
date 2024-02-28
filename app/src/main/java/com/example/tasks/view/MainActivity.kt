package com.example.tasks.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
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

    override fun onResume() {
        super.onResume()
        setupViewModel()
        setupRecyclerView()
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
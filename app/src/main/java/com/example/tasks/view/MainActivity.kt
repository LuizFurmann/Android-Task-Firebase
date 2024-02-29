package com.example.tasks.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tasks.databinding.ActivityMainBinding
import com.example.tasks.model.Task
import kotlinx.coroutines.launch
import java.lang.reflect.Field


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
        noteFilter()
    }

    private fun noteFilter(){
        binding.tvSearchTask.editText!!.addTextChangedListener {
            taskAdapter.getFilter().filter(it.toString())
        }
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
                binding.pbTask.visibility = View.GONE
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
            binding.emptyList.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
            binding.emptyList.visibility = View.GONE
            taskAdapter.updateList(trainings)
        }
    }

    private fun createNewTask(){
        binding.fabNewTask.setOnClickListener {
            Intent(this@MainActivity, CreateTaskActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        try {
            val field: Field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
            field.isAccessible = true
            field.setBoolean(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.onPrepareOptionsMenu(menu)
    }




}
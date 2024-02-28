package com.example.tasks.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tasks.model.Task
import com.example.tasks.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore

class TaskViewModel: ViewModel() {

    private val taskRepository = TaskRepository()
    private var db = FirebaseFirestore.getInstance()
    private val products = "Tasks"
    var taskList = MutableLiveData<List<Task>>()
    val getListLiveData = MutableLiveData<List<Task>>()

    init {
        taskList = taskRepository.getTasks()
    }

    fun getTasks(): MutableLiveData<List<Task>> {
        return taskList
    }

    fun createTask(task: Task) = taskRepository.createTask(task)

    fun updateTask(task: Task) = taskRepository.updateTask(task)

    fun deleteTask(id: String) = taskRepository.deleteTask(id)
}
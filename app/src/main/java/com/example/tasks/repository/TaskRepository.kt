package com.example.tasks.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.tasks.model.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {

    private val tasks = "Tasks"
    private var db = FirebaseFirestore.getInstance()
    private var responseTask = MutableLiveData<List<Task>>()

    fun getTasks():MutableLiveData<List<Task>> {
        val docRef = db.collection(tasks)
        docRef.get().addOnSuccessListener {
            val tasks = ArrayList<Task>()
            for (item in it.documents) {
                val task = Task()
                task.id = item.id
                task.title = item.data!!["title"] as String?
                task.description = item.data!!["description"] as String?
                tasks.add(task)
            }
            responseTask.postValue(tasks)
        }.addOnFailureListener {
            Log.d("get", it.localizedMessage!!)
        }
        return responseTask
    }
}
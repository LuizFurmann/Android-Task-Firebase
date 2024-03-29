package com.example.tasks.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.tasks.model.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class TaskRepository {

    private val tasks = "Tasks"
    private var db = FirebaseFirestore.getInstance()
    private var responseTask = MutableLiveData<List<Task>>()

    fun getTasks(userId: String):MutableLiveData<List<Task>> {

        val docRef = db.collection(tasks)

        docRef.whereEqualTo("userId", userId).get().addOnSuccessListener {
            val tasks = ArrayList<Task>()
            for (item in it.documents) {
                val task = Task()
                task.id = item.id
                task.title = item.data!!["title"] as String?
                task.description = item.data!!["description"] as String?
                task.image = item.data!!["image"] as String?
                tasks.add(task)
            }
            responseTask.postValue(tasks)
        }.addOnFailureListener {
            Log.d("get", it.localizedMessage!!)
        }
        return responseTask
    }

    fun createTask(task: Task) {
        val docRef = db.collection(tasks)
        docRef.add(task.toMap()).addOnSuccessListener {
        }.addOnFailureListener {
            Log.d("createTask >>>>>", it.localizedMessage!!)
        }
    }

    fun updateTask(task: Task) {
        val docRef = db.collection(tasks)
        docRef.document(task.id!!).update(task.toMap()).addOnSuccessListener {
        }.addOnFailureListener {
            Log.d("updateTask >>>>>", it.localizedMessage!!)
        }
    }

    fun deleteTask(id: String) {
        val docRef = db.collection(tasks)
        docRef.document(id).delete().addOnSuccessListener {
        }.addOnFailureListener {
            Log.d("deleteTask >>>>>", it.localizedMessage!!)
        }
    }
}
package com.example.tasks.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.databinding.RowTaskBinding
import com.example.tasks.model.Task

class TaskAdapter(private var context: Context) :
    RecyclerView.Adapter<TaskAdapter.TrainingViewHolder>() {

    private var taskList = arrayListOf<Task>()
    fun updateList(tasks: List<Task>) {
        this.taskList.clear()
        this.taskList.addAll(tasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainingViewHolder {
        val binding = RowTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.title = currentItem.title.toString()
        holder.description = currentItem.description.toString()

    }

    override fun getItemCount() = taskList.size

    class TrainingViewHolder(val binding: RowTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var title = binding.tvTitle.toString()
        var description = binding.tvDescription.toString()
    }
}
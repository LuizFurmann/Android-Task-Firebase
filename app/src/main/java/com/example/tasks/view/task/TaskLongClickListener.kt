package com.example.tasks.view.task

import com.example.tasks.model.Task

interface TaskLongClickListener {
    fun onNoteClicked(task: Task)
    fun onOptionClicked(itemId: Int)
}
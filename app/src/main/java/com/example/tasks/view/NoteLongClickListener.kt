package com.example.tasks.view

import com.example.tasks.model.Task

interface NoteLongClickListener {
    fun onNoteClicked(task: Task)
    fun onOptionClicked(itemId: Int)
}
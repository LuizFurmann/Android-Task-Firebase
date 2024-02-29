package com.example.tasks.view.task

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.R
import com.example.tasks.StringHelper
import com.example.tasks.databinding.RowTaskBinding
import com.example.tasks.model.Task
import java.util.Locale

class TaskAdapter(private val clickListener: TaskLongClickListener, private var context: Context) :
    RecyclerView.Adapter<TaskAdapter.TrainingViewHolder>() {

    private var taskList = arrayListOf<Task>()
    private var tasksFiltered = arrayListOf<Task>()

    fun updateList(tasks: List<Task>) {
        this.taskList.clear()
        this.taskList.addAll(tasks)
        this.tasksFiltered.clear()
        this.tasksFiltered.addAll(tasks)
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
        holder.binding.tvTitle.text = currentItem.title.toString()
        holder.binding.tvDescription.text = currentItem.description.toString()

        holder.itemView.setOnClickListener {
            Intent(context, TaskDetailsActivity::class.java).also {
                it.putExtra(CreateTaskActivity.REQ_EDIT, true)
                it.putExtra(CreateTaskActivity.EXTRA_DATA, currentItem)
                context.startActivity(it)
            }
        }

        holder.itemView.setOnLongClickListener {

            clickListener.onNoteClicked(currentItem)

            val pop= PopupMenu(context,it)
            pop.inflate(R.menu.menu_main)
            pop.setOnMenuItemClickListener {item->
                when(item.itemId)
                {
                    R.id.editNote->{
                        Intent(context, CreateTaskActivity::class.java).also {
                            it.putExtra(CreateTaskActivity.REQ_EDIT, true)
                            it.putExtra(CreateTaskActivity.EXTRA_DATA, currentItem)
                            context.startActivity(it)
                        }
                    }
                    R.id.deleteNote->{
                        clickListener.onOptionClicked(item.itemId)
                    }
                }
                true
            }
            pop.show()
            true
        }
    }

    override fun getItemCount() = taskList.size

    class TrainingViewHolder(val binding: RowTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var title = binding.tvTitle.text
        var description = binding.tvDescription.text
    }

    fun getFilter(): Filter {
        val filter : Filter
        filter = object : Filter(){
            override fun performFiltering(filter: CharSequence): FilterResults {
                var filter = filter
                val results = FilterResults()

                if (filter.isEmpty()) {
                    results.count = tasksFiltered.size
                    results.values = tasksFiltered
                } else {
                    val filteredItems = arrayListOf<Task>()
                    for (i in tasksFiltered.indices) {

                        filter = StringHelper.removeDiacriticalMarks(
                            filter.toString().toLowerCase(Locale.ROOT)
                        )

                        val data = tasksFiltered[i]
                        val nameSearch = StringHelper.getFilterText(data.title)

                        if(nameSearch.contains(filter)) {
                            filteredItems.add(data)
                        }
                    }
                    results.count = filteredItems.size
                    results.values = filteredItems
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                taskList = results.values as ArrayList<Task>
                notifyDataSetChanged()
            }
        }
        return filter
    }
}
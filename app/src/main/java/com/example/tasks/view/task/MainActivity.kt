package com.example.tasks.view.task

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tasks.R
import com.example.tasks.databinding.ActivityMainBinding
import com.example.tasks.model.Task
import com.example.tasks.view.authentication.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.lang.reflect.Field


class MainActivity : AppCompatActivity(), TaskLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private var taskAdapter = TaskAdapter(this, this)
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    var clickedItem = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = ""

        setupNavHeader()
        setupDrawer()
        setupViewModel()
        setupRecyclerView()
        createNewTask()
        noteFilter()
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupNavHeader() {
        val user = FirebaseAuth.getInstance().currentUser!!
        val headerView = binding.navView.getHeaderView(0)
        val navUsername = headerView.findViewById<View>(R.id.userEmail) as TextView
        navUsername.text = user.displayName.toString()
    }

    private fun setupDrawer() {
        val draweLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        toggle = ActionBarDrawerToggle(this, draweLayout, R.string.open, R.string.close)
        draweLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> logout()
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun noteFilter(){
        binding.tvSearchTask.editText!!.addTextChangedListener {
            taskAdapter.getFilter().filter(it.toString())
        }
    }

    private fun setupViewModel(){
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        taskViewModel.getTasks(currentUserID).observe(this) { task ->
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

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Intent(this@MainActivity, LoginActivity::class.java).also {
            startActivity(it)
            finish()
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

    override fun onNoteClicked(task: Task) {
        clickedItem = task.id.toString()
    }

    override fun onOptionClicked(itemId: Int) {
        if(itemId == R.id.deleteNote){
            val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
            builder.setMessage("Deseja deletar a tarefa?")
            builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                taskViewModel.deleteTask(clickedItem)
                setupViewModel()
                setupRecyclerView()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            }
            builder.show()
        }
    }
}
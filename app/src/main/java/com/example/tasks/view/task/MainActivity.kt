package com.example.tasks.view.task

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tasks.R
import com.example.tasks.databinding.ActivityMainBinding
import com.example.tasks.databinding.DialogTaskBinding
import com.example.tasks.model.Task
import com.example.tasks.view.authentication.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.lang.reflect.Field


class MainActivity : AppCompatActivity(), TaskLongClickListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var bindingDialog: DialogTaskBinding

    private var taskAdapter = TaskAdapter(this, this)
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    private var profilePicturePath: String? = null
    private val REQUEST_IMAGE = 100

    lateinit var taskAux: Task

    var clickedItem = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        bindingDialog = DialogTaskBinding.inflate(layoutInflater)
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = ""

        setupNavHeader()
        setupDrawer()
        setupViewModel()
        setupRecyclerView()
        noteFilter()

        binding.fabNewTask.setOnClickListener {
            dialogCreateTask()
        }
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
        navUsername.text = user.email.toString()
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

    private fun dialogCreateTask() {
        val dialogView = bindingDialog.root
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        bindingDialog.selectedImage.setOnClickListener {
            imageChooser()
        }

        bindingDialog.btnSaveTask.setOnClickListener {
            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
            requiredFields()

            if (validadeMuscleGroup()) {
                var task = Task()
                val newTask = Task(
                    task.id,
                    currentUserID,
                    bindingDialog.etTaskTitle.text.toString(),
                    bindingDialog.etTaskDescription.text.toString(),
                    profilePicturePath
                )
                taskViewModel.createTask(newTask)
                setupViewModel()
                setupRecyclerView()
            }

            dialog.dismiss()
        }
    }

    private fun dialogUpdateTask(task: Task) {
        bindingDialog = DialogTaskBinding.inflate(layoutInflater)
        val dialogView = bindingDialog.root
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        bindingDialog.selectedImage.setOnClickListener {
            imageChooser()
        }

        if (task.image != null) {
            var myUri: Uri = Uri.parse(task.image);
            profilePicturePath = myUri.toString()
            bindingDialog.selectedImage.setImageURI(myUri)
        }
        bindingDialog.etTaskTitle.setText(task.title)

        bindingDialog.btnSaveTask.setOnClickListener {
            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
            requiredFields()
            if (validadeMuscleGroup()) {
                val taskUpdate = Task(
                    task.id,
                    currentUserID,
                    bindingDialog.etTaskTitle.text.toString(),
                    bindingDialog.etTaskDescription.text.toString(),
                    profilePicturePath
                )
                taskViewModel.updateTask(taskUpdate)
                setupViewModel()
                setupRecyclerView()
            }

            dialog.dismiss()
        }
    }

    private fun imageChooser() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(
            Intent.createChooser(i, getString(R.string.selectImage)),
            REQUEST_IMAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            val selectedImageUri = data?.data
            if (null != selectedImageUri) {

                val rPermPersist = Intent.FLAG_GRANT_READ_URI_PERMISSION
                this.contentResolver.takePersistableUriPermission(data?.data!!, rPermPersist)
                bindingDialog.selectedImage.setImageURI(data?.data)
                profilePicturePath = selectedImageUri.toString()
            }
        }
    }

    fun requiredFields() {
        val context = this
        val colorState = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_enabled)),
            intArrayOf(
                ContextCompat.getColor(context, R.color.red)
            )
        )

        val colorStateValid = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_enabled)),
            intArrayOf(
                ContextCompat.getColor(context, R.color.grey)
            )
        )

        if (bindingDialog.tilTaskTitle.editText?.text.toString().isNullOrEmpty()) {
            bindingDialog.tilTaskTitle.editText?.error = "Campo obrigatório"
            bindingDialog.tilTaskTitle.setBoxStrokeColorStateList(colorState)
            bindingDialog.tilTaskTitle.hintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
        } else {
            bindingDialog.tilTaskTitle.editText?.error = null
            bindingDialog.tilTaskTitle.setBoxStrokeColorStateList(colorStateValid)
            bindingDialog.tilTaskTitle.hintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey))
        }
    }

    fun validadeMuscleGroup(): Boolean {
        if (bindingDialog.tilTaskTitle.editText?.text.toString().isEmpty()) {
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun noteFilter() {
        binding.tvSearchTask.editText!!.addTextChangedListener {
            taskAdapter.getFilter().filter(it.toString())
        }
    }

    private fun setupViewModel() {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        taskViewModel.getTasks(currentUserID).observe(this) { task ->
            lifecycleScope.launch {
                updateList(task)
                binding.pbTask.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvTasks.adapter = taskAdapter
        binding.rvTasks.layoutManager = GridLayoutManager(this, 2)
    }

    private fun updateList(trainings: List<Task>) {
        if (trainings.isEmpty()) {
            binding.rvTasks.visibility = View.GONE
            binding.emptyList.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
            binding.emptyList.visibility = View.GONE
            taskAdapter.updateList(trainings)
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
        taskAux = task
    }

    override fun onOptionClicked(itemId: Int) {
        when (itemId) {
            R.id.editTask -> {
                dialogUpdateTask(taskAux)
            }

            R.id.deleteTask -> {
                val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
                builder.setMessage("Deseja deletar a tarefa?")
                builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    taskViewModel.deleteTask(taskAux.id.toString())
                    setupViewModel()
                    setupRecyclerView()
                }
                builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                }
                builder.show()
            }
        }
    }
}
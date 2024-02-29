package com.example.tasks.view.authentication

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.tasks.R
import com.example.tasks.databinding.ActivitySignUpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        saveUser()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun saveUser() {
        binding.btnRegister.setOnClickListener {

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

            if (binding.tilEmail.editText?.text.toString().isNullOrEmpty()) {
                binding.tilEmail.editText?.error = getString(R.string.requiredField)
            } else {
                binding.tilEmail.editText?.error = null
            }

            if (binding.tilPassword.editText?.text.toString().isNullOrEmpty()) {
                binding.tilPassword.editText?.error = getString(R.string.requiredField)
            } else {
                binding.tilPassword.editText?.error = null
            }

            if (binding.tilPasswordConfirmation.editText?.text.toString() != binding.tilPassword.editText?.text.toString()) {
                binding.tilPasswordConfirmation.editText?.error = getString(R.string.worngPassword)
            } else {
                binding.tilPasswordConfirmation.editText?.error = null
            }

            if (userValidation()) {
                auth.createUserWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            finish()
                        } else {
                            registerError()
                        }
                    }
            }
        }
    }

    private fun registerError() {
        val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        builder.setMessage(getString(R.string.registerError))
        builder.setPositiveButton(getString(R.string.understand)) { dialog, which ->
        }
        builder.show()
    }

    private fun userValidation(): Boolean {
        if (binding.tilEmail.editText?.text.toString().isNullOrEmpty()) {
            return false
        }

        if (binding.tilPassword.editText?.text.toString().isNullOrEmpty()) {
            return false
        }

        if(binding.tilPassword.editText?.text.toString() != binding.tilPasswordConfirmation.editText?.text.toString()){
            return false
        }
        return true
    }
}
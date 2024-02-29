package com.example.tasks.view.authentication

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.core.content.ContextCompat
import com.example.tasks.R
import com.example.tasks.databinding.ActivityLoginBinding
import com.example.tasks.view.task.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        loginAction()
        createAccount()

        val mSpannableString = SpannableString("Criar")
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        binding.tvCreateAccount.text = mSpannableString
    }

    private fun loginAction() {
        binding.btnLogin.setOnClickListener {

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

            if (loginValidation()) {
                auth.signInWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Intent(this@LoginActivity, MainActivity::class.java).also {
                                startActivity(it)
                                finish()
                            }
                        } else {
                            loginError()
                        }
                    }
            }
        }
    }

    private fun loginError() {
        val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
        builder.setMessage(getString(R.string.invalidUserOrPassword))
        builder.setPositiveButton(getString(R.string.understand)) { dialog, which ->
        }
        builder.show()
    }

    private fun loginValidation(): Boolean {
        if (binding.tilEmail.editText?.text.toString().isNullOrEmpty()) {
            return false
        } else if (binding.tilPassword.editText?.text.toString().isNullOrEmpty()) {
            return false
        }
        return true
    }

    private fun createAccount(){
        binding.tvCreateAccount.setOnClickListener {
            Intent(this@LoginActivity, SignUpActivity::class.java).also{
                startActivity(it)
            }
        }
    }
}
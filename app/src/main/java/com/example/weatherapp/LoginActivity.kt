package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        // 1. Verificar si el usuario ya inició sesión
        if (firebaseAuth.currentUser != null) {
            goToMainActivity()
        }
    }

    private fun setupListeners() {
        // --- Listener para CREAR CUENTA (SIGN UP) ---
        binding.btnSignUp.setOnClickListener {
            handleSignUp()
        }

        // --- Listener para INICIAR SESIÓN (LOGIN) ---
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        binding.tvStatus.text = ""

        if (email.isEmpty() || pass.isEmpty()) {
            binding.tvStatus.text = "Por favor, completa ambos campos."
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "¡Registro Exitoso!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthUserCollisionException -> "El correo ya está registrado."
                        else -> "Error: ${task.exception?.localizedMessage ?: "Fallo desconocido"}"
                    }
                    Log.e("FIREBASE_AUTH", "Sign Up Failed: $errorMessage")
                    // Capitalize fue deprecado, se usa replaceFirstChar en su lugar
                    binding.tvStatus.text = errorMessage.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    }
                }
            }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        binding.tvStatus.text = ""

        if (email.isEmpty() || pass.isEmpty()) {
            binding.tvStatus.text = "Por favor, ingresa correo y contraseña."
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "¡Bienvenido de vuelta!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    Log.e("FIREBASE_AUTH", "Login Failed: ${task.exception?.localizedMessage}")
                    binding.tvStatus.text = "Error de inicio de sesión. Verifica tus credenciales."
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

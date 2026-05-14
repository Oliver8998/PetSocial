package com.example.petsocial.Login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.petsocial.Models.Colecciones
import com.example.petsocial.Models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val TAG = "LoginVM"

    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        _isLoading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d(TAG, "Login exitoso: ${auth.currentUser?.email}")
                    Toast.makeText(context, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Log.e(TAG, "Error login", task.exception)
                    _errorMessage.value = task.exception?.message
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun registro(
        email: String,
        password: String,
        nombre: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        _isLoading.value = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val nuevoUsuario = Usuario(
                        id = userId,
                        email = email,
                        nombre = nombre,
                        rol = "USER"
                    )

                    db.collection(Colecciones.usuarios)
                        .document(userId)
                        .set(nuevoUsuario)
                        .addOnSuccessListener {
                            _isLoading.value = false
                            Log.d(TAG, "Usuario creado: $email")
                            Toast.makeText(context, "Cuenta creada!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            Log.e(TAG, "Error guardando usuario", e)
                            _errorMessage.value = e.message
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    _isLoading.value = false
                    Log.e(TAG, "Error registro", task.exception)
                    _errorMessage.value = task.exception?.message
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signOut(context: Context) {
        auth.signOut()
        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Sesión cerrada")
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
}
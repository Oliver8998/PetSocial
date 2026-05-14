package com.example.petsocial.MisMascotas

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.petsocial.Models.Colecciones
import com.example.petsocial.Models.Mascota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MascotasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _misMascotas = mutableStateListOf<Mascota>()
    val misMascotas: List<Mascota> get() = _misMascotas

    var mascotaSeleccionada: Mascota? = null

    val TAG = "MascotasVM"

    fun cargarMisMascotas() {
        val userId = auth.currentUser?.uid ?: return

        _isLoading.value = true

        db.collection(Colecciones.mascotas)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val mascotasList = result.mapNotNull { document ->
                    document.toObject(Mascota::class.java).apply {
                        id = document.id
                    }
                }
                _misMascotas.clear()
                _misMascotas.addAll(mascotasList)
                _isLoading.value = false
                Log.d(TAG, "Mascotas cargadas: ${mascotasList.size}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando mascotas", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }

    fun crearMascota(mascota: Mascota) {
        _isLoading.value = true

        val userId = auth.currentUser?.uid ?: return
        val nuevaMascota = mascota.copy(userId = userId)

        db.collection(Colecciones.mascotas)
            .add(nuevaMascota)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Mascota creada con ID: ${documentReference.id}")
                cargarMisMascotas()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error creando mascota", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }

    fun actualizarMascota(mascota: Mascota) {
        db.collection(Colecciones.mascotas)
            .document(mascota.id)
            .set(mascota)
            .addOnSuccessListener {
                Log.d(TAG, "Mascota actualizada")
                cargarMisMascotas()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error actualizando mascota", e)
                _errorMessage.value = e.message
            }
    }

    fun borrarMascota(mascota: Mascota) {
        _isLoading.value = true

        db.collection(Colecciones.mascotas)
            .document(mascota.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Mascota borrada")
                cargarMisMascotas()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error borrando mascota", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }
}
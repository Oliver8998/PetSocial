package com.example.petsocial.Feed

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.petsocial.Models.Colecciones
import com.example.petsocial.Models.Mascota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FeedViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _todasMascotas = mutableStateListOf<Mascota>()
    val todasMascotas: List<Mascota> get() = _todasMascotas

    var mascotaSeleccionada by mutableStateOf<Mascota?>(null)

    val TAG = "FeedVM"

    fun cargarTodasMascotas() {
        _isLoading.value = true

        db.collection(Colecciones.mascotas)
            .get()
            .addOnSuccessListener { result ->
                val mascotasList = result.mapNotNull { document ->
                    document.toObject(Mascota::class.java).apply {
                        id = document.id
                    }
                }
                _todasMascotas.clear()
                _todasMascotas.addAll(mascotasList)
                _isLoading.value = false
                Log.d(TAG, "Mascotas cargadas: ${mascotasList.size}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando mascotas", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }

    fun darLike(mascota: Mascota) {
        val userId = auth.currentUser?.uid ?: return

        val likedBy = mascota.likedBy.toMutableList()

        if (likedBy.contains(userId)) {
            likedBy.remove(userId)
        } else {
            likedBy.add(userId)
        }

        val mascotaActualizada = mascota.copy(
            likes = likedBy.size,
            likedBy = likedBy
        )

        db.collection(Colecciones.mascotas)
            .document(mascota.id)
            .set(mascotaActualizada)
            .addOnSuccessListener {
                Log.d(TAG, "Like actualizado")
                val index = _todasMascotas.indexOfFirst { it.id == mascota.id }
                if (index != -1) {
                    _todasMascotas[index] = mascotaActualizada
                }
                mascotaSeleccionada = mascotaActualizada            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error actualizando like", e)
                _errorMessage.value = e.message
            }
    }

    fun yaLeDioLike(mascota: Mascota): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return mascota.likedBy.contains(userId)
    }
}
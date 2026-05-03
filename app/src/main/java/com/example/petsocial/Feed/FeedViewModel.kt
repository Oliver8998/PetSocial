package com.example.petsocial.Feed

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petsocial.Models.Colecciones
import com.example.petsocial.Models.Mascota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _todasMascotas = mutableStateListOf<Mascota>()
    val todasMascotas: List<Mascota> get() = _todasMascotas

    private val _mascotaDetalle = MutableLiveData<Mascota?>()
    val mascotaDetalle: LiveData<Mascota?> = _mascotaDetalle

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

    fun cargarMascotaPorId(mascotaId: String) {
        db.collection(Colecciones.mascotas)
            .document(mascotaId)
            .get()
            .addOnSuccessListener { document ->
                val mascota = document.toObject(Mascota::class.java)?.apply {
                    id = document.id
                }
                _mascotaDetalle.value = mascota
                Log.d(TAG, "Mascota detalle cargada: ${mascota?.nombre}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando detalle", e)
                _errorMessage.value = e.message
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

        mascota.likes = likedBy.size
        mascota.likedBy = likedBy

        db.collection(Colecciones.mascotas)
            .document(mascota.id)
            .set(mascota)
            .addOnSuccessListener {
                Log.d(TAG, "Like actualizado")
                cargarTodasMascotas()
                cargarMascotaPorId(mascota.id)
            }
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
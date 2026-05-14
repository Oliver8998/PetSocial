package com.example.petsocial.Ranking

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.petsocial.Models.Colecciones
import com.example.petsocial.Models.Mascota
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RankingViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _todasMascotas = mutableStateListOf<Mascota>()

    private val _mascotasRanking = mutableStateListOf<Mascota>()
    val mascotasRanking: List<Mascota> get() = _mascotasRanking

    val filtroEspecie = MutableStateFlow("TODAS")

    val TAG = "RankingVM"

    fun cargarRanking() {
        _isLoading.value = true

        db.collection(Colecciones.mascotas)
            .orderBy("likes", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val mascotasList = result.mapNotNull { document ->
                    document.toObject(Mascota::class.java).apply {
                        id = document.id
                    }
                }
                _todasMascotas.clear()
                _todasMascotas.addAll(mascotasList)
                aplicarFiltro()
                _isLoading.value = false
                Log.d(TAG, "Ranking cargado: ${mascotasList.size} mascotas")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando ranking", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }

    fun setFiltro(especie: String) {
        filtroEspecie.value = especie
        aplicarFiltro()
    }

    fun getEspecies(): List<String> {
        return _todasMascotas.map { it.especie }.distinct().sorted()
    }

    private fun aplicarFiltro() {
        _mascotasRanking.clear()
        if (filtroEspecie.value == "TODAS") {
            _mascotasRanking.addAll(_todasMascotas)
        } else {
            _mascotasRanking.addAll(
                _todasMascotas.filter { it.especie.lowercase() == filtroEspecie.value.lowercase() }
            )
        }
    }
}
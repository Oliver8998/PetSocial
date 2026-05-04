package com.example.petsocial.Ranking

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petsocial.Models.Colecciones
import com.example.petsocial.Models.Mascota
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RankingViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _mascotasRanking = mutableStateListOf<Mascota>()
    val mascotasRanking: List<Mascota> get() = _mascotasRanking

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
                _mascotasRanking.clear()
                _mascotasRanking.addAll(mascotasList)
                _isLoading.value = false
                Log.d(TAG, "Ranking cargado: ${mascotasList.size} mascotas")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando ranking", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }
}
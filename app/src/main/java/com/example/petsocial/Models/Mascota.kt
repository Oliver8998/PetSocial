package com.example.petsocial.Models

data class Mascota(
    var id: String = "",
    var nombre: String = "",
    var especie: String = "",
    var edad: Int = 0,
    var descripcion: String = "",
    var imageUrl: String = "",
    var userId: String = "",
    var likes: Int = 0,
    var likedBy: List<String> = emptyList()
)
package com.example.petsocial.MisMascotas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.petsocial.Models.Mascota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotaFormScreen(
    mascotasViewModel: MascotasViewModel,
    navController: NavHostController
) {
    val mascotaEditar = mascotasViewModel.mascotaSeleccionada
    val esEdicion = mascotaEditar != null

    var nombre by remember { mutableStateOf(mascotaEditar?.nombre ?: "") }
    var especie by remember { mutableStateOf(mascotaEditar?.especie ?: "") }
    var edad by remember { mutableStateOf(mascotaEditar?.edad?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(mascotaEditar?.descripcion ?: "") }

    val isLoading by mascotasViewModel.isLoading.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Mascota" else "Nueva Mascota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie (Perro, Gato, etc.)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad (años)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = {
                    val edadInt = edad.toIntOrNull() ?: 0

                    if (esEdicion) {
                        val mascotaActualizada = mascotaEditar.copy(
                            nombre = nombre,
                            especie = especie,
                            edad = edadInt,
                            descripcion = descripcion
                        )
                        mascotasViewModel.actualizarMascota(mascotaActualizada)
                    } else {
                        val nuevaMascota = Mascota(
                            nombre = nombre,
                            especie = especie,
                            edad = edadInt,
                            descripcion = descripcion
                        )
                        mascotasViewModel.crearMascota(nuevaMascota)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && especie.isNotBlank() && !isLoading
            ) {
                Text(if (esEdicion) "Actualizar" else "Crear")
            }
        }
    }
}
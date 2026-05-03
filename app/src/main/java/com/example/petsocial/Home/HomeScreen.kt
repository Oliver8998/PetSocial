package com.example.petsocial.Home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.petsocial.Login.LoginViewModel
import com.example.petsocial.Models.Rutas

@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🐾 Bienvenido a PetSocial 🐾",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text("Estás logueado correctamente")

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate(Rutas.FEED)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Feed")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Rutas.MIS_MASCOTAS)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis Mascotas")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                loginViewModel.signOut(context)
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(0)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }
}
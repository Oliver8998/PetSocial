package com.example.petsocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petsocial.Home.HomeScreen
import com.example.petsocial.Login.LoginScreen
import com.example.petsocial.Login.LoginViewModel
import com.example.petsocial.Models.Rutas
import com.example.petsocial.ui.theme.PetSocialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetSocialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    // Determinar pantalla inicial
    val startDestination = if (loginViewModel.isUserLoggedIn()) {
        Rutas.HOME
    } else {
        Rutas.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                loginViewModel = loginViewModel,
                navController = navController
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                loginViewModel = loginViewModel,
                navController = navController
            )
        }
    }
}
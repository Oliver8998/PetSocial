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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petsocial.Detalle.DetalleScreen
import com.example.petsocial.Feed.FeedScreen
import com.example.petsocial.Feed.FeedViewModel
import com.example.petsocial.Home.HomeScreen
import com.example.petsocial.Home.MainScreen
import com.example.petsocial.Login.LoginScreen
import com.example.petsocial.Login.LoginViewModel
import com.example.petsocial.MisMascotas.MascotaFormScreen
import com.example.petsocial.MisMascotas.MascotasViewModel
import com.example.petsocial.MisMascotas.MisMascotasScreen
import com.example.petsocial.Models.Rutas
import com.example.petsocial.Ranking.RankingScreen
import com.example.petsocial.Ranking.RankingViewModel
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
    val mascotasViewModel: MascotasViewModel = viewModel()
    val feedViewModel: FeedViewModel = viewModel()
    val rankingViewModel: RankingViewModel = viewModel()

    val startDestination = if (loginViewModel.isUserLoggedIn()) {
        Rutas.MAIN
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

        composable(Rutas.MAIN) {
            MainScreen(
                loginViewModel = loginViewModel,
                mascotasViewModel = mascotasViewModel,
                feedViewModel = feedViewModel,
                rankingViewModel = rankingViewModel,
                mainNavController = navController
            )
        }

        composable(Rutas.MASCOTA_FORM) {
            MascotaFormScreen(
                mascotasViewModel = mascotasViewModel,
                navController = navController
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("mascotaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mascotaId = backStackEntry.arguments?.getString("mascotaId") ?: ""
            DetalleScreen(
                feedViewModel = feedViewModel,
                navController = navController,
                mascotaId = mascotaId
            )
        }
    }
}
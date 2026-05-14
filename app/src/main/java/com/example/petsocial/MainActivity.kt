package com.example.petsocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petsocial.Detalle.DetalleScreen
import com.example.petsocial.Feed.FeedViewModel
import com.example.petsocial.Home.MainScreen
import com.example.petsocial.Login.LoginScreen
import com.example.petsocial.Login.LoginViewModel
import com.example.petsocial.MisMascotas.MascotaFormScreen
import com.example.petsocial.MisMascotas.MascotasViewModel
import com.example.petsocial.Models.Rutas
import com.example.petsocial.Ranking.RankingViewModel
import com.example.petsocial.ui.theme.PetSocialTheme

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val mascotasViewModel: MascotasViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()
    private val rankingViewModel: RankingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetSocialTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    mascotasViewModel = mascotasViewModel,
                    feedViewModel = feedViewModel,
                    rankingViewModel = rankingViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    mascotasViewModel: MascotasViewModel,
    feedViewModel: FeedViewModel,
    rankingViewModel: RankingViewModel
) {
    val startDestination = if (loginViewModel.isUserLoggedIn()) Rutas.MAIN else Rutas.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
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

        composable(Rutas.DETALLE) {
            DetalleScreen(
                feedViewModel = feedViewModel,
                navController = navController
            )
        }
    }
}
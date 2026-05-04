package com.example.petsocial.Home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.petsocial.Feed.FeedScreen
import com.example.petsocial.Feed.FeedViewModel
import com.example.petsocial.Login.LoginViewModel
import com.example.petsocial.MisMascotas.MascotasViewModel
import com.example.petsocial.MisMascotas.MisMascotasScreen
import com.example.petsocial.Models.Rutas
import com.example.petsocial.Ranking.RankingScreen
import com.example.petsocial.Ranking.RankingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    mascotasViewModel: MascotasViewModel,
    feedViewModel: FeedViewModel,
    rankingViewModel: RankingViewModel,
    mainNavController: NavHostController
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))

                Text(
                    "🐾 PetSocial",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )

                Divider()

                Spacer(Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        // TODO: Implementar perfil
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        loginViewModel.signOut(context)
                        mainNavController.navigate(Rutas.LOGIN) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PetSocial") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Feed") },
                        selected = currentRoute == "bottom_feed",
                        onClick = {
                            navController.navigate("bottom_feed") {
                                popUpTo("bottom_feed") { inclusive = true }
                            }
                        }
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Pets, contentDescription = null) },
                        label = { Text("Mis Mascotas") },
                        selected = currentRoute == "bottom_mascotas",
                        onClick = {
                            navController.navigate("bottom_mascotas") {
                                popUpTo("bottom_feed") { saveState = true }
                            }
                        }
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                        label = { Text("Ranking") },
                        selected = currentRoute == "bottom_ranking",
                        onClick = {
                            navController.navigate("bottom_ranking") {
                                popUpTo("bottom_feed") { saveState = true }
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "bottom_feed",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("bottom_feed") {
                    FeedScreen(
                        feedViewModel = feedViewModel,
                        navController = mainNavController
                    )
                }

                composable("bottom_mascotas") {
                    MisMascotasScreen(
                        mascotasViewModel = mascotasViewModel,
                        navController = mainNavController
                    )
                }

                composable("bottom_ranking") {
                    RankingScreen(
                        rankingViewModel = rankingViewModel,
                        navController = mainNavController
                    )
                }
            }
        }
    }
}
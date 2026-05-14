package com.example.petsocial.Home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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

    var mostrarMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "PetSocial",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )

                HorizontalDivider()

                Spacer(Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    label = { Text(text = "Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
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
                    title = { Text(text = "PetSocial") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = mostrarMenu,
                            onDismissRequest = { mostrarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Cerrar Sesión") },
                                onClick = {
                                    mostrarMenu = false
                                    loginViewModel.signOut(context)
                                    mainNavController.navigate(Rutas.LOGIN) {
                                        popUpTo(0)
                                    }
                                }
                            )
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
                        label = { Text(text = "Feed") },
                        selected = currentRoute == Rutas.BOTTOM_FEED,
                        onClick = {
                            navController.navigate(Rutas.BOTTOM_FEED) {
                                popUpTo(Rutas.BOTTOM_FEED) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Pets, contentDescription = null) },
                        label = { Text(text = "Mis Mascotas") },
                        selected = currentRoute == Rutas.BOTTOM_MASCOTAS,
                        onClick = {
                            navController.navigate(Rutas.BOTTOM_MASCOTAS) {
                                popUpTo(Rutas.BOTTOM_FEED) { saveState = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                        label = { Text(text = "Ranking") },
                        selected = currentRoute == Rutas.BOTTOM_RANKING,
                        onClick = {
                            navController.navigate(Rutas.BOTTOM_RANKING) {
                                popUpTo(Rutas.BOTTOM_FEED) { saveState = true }
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Rutas.BOTTOM_FEED,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Rutas.BOTTOM_FEED) {
                    FeedScreen(
                        feedViewModel = feedViewModel,
                        navController = mainNavController
                    )
                }
                composable(Rutas.BOTTOM_MASCOTAS) {
                    MisMascotasScreen(
                        mascotasViewModel = mascotasViewModel,
                        navController = mainNavController
                    )
                }
                composable(Rutas.BOTTOM_RANKING) {
                    RankingScreen(
                        rankingViewModel = rankingViewModel,
                        navController = mainNavController
                    )
                }
            }
        }
    }
}
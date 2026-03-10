package edu.javeriana.fixup.ui.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.javeriana.fixup.navigation.AppNavigation
import edu.javeriana.fixup.navigation.AppScreens
import edu.javeriana.fixup.ui.BottomNavigationBar
import edu.javeriana.fixup.ui.model.MockPropertyRepository
import edu.javeriana.fixup.ui.theme.FixUpTheme
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val uiState by viewModel.uiState.collectAsState()

    // Sincronizar la ruta actual con el ViewModel
    LaunchedEffect(currentRoute) {
        viewModel.updateCurrentRoute(currentRoute)
    }

    // Lógica para Bottom Bar especial (como la de Property Detail)
    val specialBottomBar: @Composable () -> Unit = {
        if (currentRoute?.startsWith(AppScreens.PropertyDetail.route) == true) {
            val propertyId = navBackStackEntry?.arguments?.getString("propertyId")
            val property = remember(propertyId) {
                MockPropertyRepository.getProperties().find { it.id == propertyId }
            }
            val currencyFormat = remember {
                NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                    maximumFractionDigits = 0
                }
            }

            if (property != null) {
                BottomAppBar(
                    containerColor = Color.White,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${currencyFormat.format(property.price)} $",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Por mes",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Button(
                            onClick = { 
                                navController.navigate(AppScreens.Checkout.route)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Reservar ahora")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (uiState.topBarTitle != null) {
                TopAppBar(
                    title = { Text(uiState.topBarTitle!!) },
                    navigationIcon = {
                        if (uiState.showBackButton) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (uiState.showBottomNav) {
                BottomNavigationBar(navController = navController)
            } else {
                specialBottomBar()
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FixUpTheme {
        MainScreen()
    }
}

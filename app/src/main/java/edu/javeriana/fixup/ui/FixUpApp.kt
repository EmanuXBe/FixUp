package edu.javeriana.fixup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.javeriana.fixup.navigation.AppNavigation
import edu.javeriana.fixup.navigation.AppScreens
import edu.javeriana.fixup.ui.features.main.MainViewModel
import edu.javeriana.fixup.ui.features.property_detail.PropertyDetailViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixUpApp(
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

    Scaffold(
        topBar = {
            if (uiState.topBarTitle != null) {
                TopAppBar(
                    title = { Text(uiState.topBarTitle!!) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (uiState.showBottomNav) {
                BottomNavigationBar(navController = navController)
            } else {
                SpecialBottomBar(currentRoute, navBackStackEntry?.arguments?.getString("propertyId")) {
                    navController.navigate(AppScreens.Checkout.route)
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SpecialBottomBar(
    currentRoute: String?,
    propertyId: String?,
    onReserveClick: () -> Unit
) {
    if (currentRoute?.startsWith(AppScreens.PropertyDetail.route) == true && propertyId != null) {
        // Usamos el ViewModel para obtener la propiedad en lugar del repositorio directamente
        val propertyViewModel: PropertyDetailViewModel = hiltViewModel()
        val detailUiState by propertyViewModel.uiState.collectAsState()

        LaunchedEffect(propertyId) {
            propertyViewModel.loadProperty(propertyId)
        }

        val currencyFormat = remember {
            NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                maximumFractionDigits = 0
            }
        }

        val property = detailUiState.property

        if (property != null) {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
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
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Por mes",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onReserveClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Reservar ahora")
                    }
                }
            }
        }
    }
}

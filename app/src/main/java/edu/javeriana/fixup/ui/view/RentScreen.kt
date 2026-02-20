package edu.javeriana.fixup.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.javeriana.fixup.ui.BottomNavigationBar
import edu.javeriana.fixup.ui.model.*

@Composable
fun RentScreen(
    navController: NavController,
    viewModel: RentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                RentHeader()
                FilterControls(resultCount = if (uiState is RentUiState.Success) (uiState as RentUiState.Success).properties.size else 0)
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is RentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RentUiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 1. Map View (Full background)
                        MapAreaPlaceholder(
                            properties = state.properties,
                            selectedPropertyId = state.properties.firstOrNull()?.id,
                            modifier = Modifier.fillMaxSize()
                        )

                        // 2. Property Card (Floating at the bottom)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 250.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.properties) { property ->
                                PropertyCard(
                                    property = property,
                                    onSelectClick = { viewModel.onPropertySelected(property.id) }
                                )
                            }
                        }
                    }
                }
                is RentUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

package edu.javeriana.fixup.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import edu.javeriana.fixup.ui.features.rent.CreatePropertyScreen
import edu.javeriana.fixup.ui.features.rent.CreatePropertyViewModel

/**
 * Pantalla de publicación legacy — delega a CreatePropertyScreen.
 * No está registrada en el nav graph; CreatePropertyScreen es la ruta activa.
 */
@Composable
fun PublicationScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreatePropertyViewModel = hiltViewModel()
) {
    CreatePropertyScreen(
        onBackClick = onBackClick,
        onSuccess   = onSuccess,
        viewModel   = viewModel
    )
}

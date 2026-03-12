package edu.javeriana.fixup.ui.features.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.javeriana.fixup.ui.FixUpApp
import edu.javeriana.fixup.ui.theme.FixUpTheme

/**
 * MainScreen is now a wrapper for FixUpApp.
 * This file is kept to maintain compatibility with existing references.
 */
@Composable
fun MainScreen() {
    FixUpApp()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FixUpTheme {
        MainScreen()
    }
}

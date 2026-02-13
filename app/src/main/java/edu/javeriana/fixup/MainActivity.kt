package edu.javeriana.fixup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.javeriana.fixup.navigation.AppNavigation
import edu.javeriana.fixup.ui.theme.FixUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixUpTheme {
                AppNavigation()
            }
        }
    }
}
package edu.javeriana.fixup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import edu.javeriana.fixup.ui.FixUpApp
import edu.javeriana.fixup.ui.theme.FixUpTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixUpTheme {
                FixUpApp()
            }
        }
    }
}

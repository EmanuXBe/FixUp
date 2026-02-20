package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.javeriana.fixup.R
import edu.javeriana.fixup.navigation.AppScreens
import edu.javeriana.fixup.ui.theme.FixUpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            selected = currentDestination?.route == AppScreens.Feed.route,
            onClick = { 
                navController.navigate(AppScreens.Feed.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
            selected = currentDestination?.route == AppScreens.Rent.route,
            onClick = { 
                navController.navigate(AppScreens.Rent.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.AddBox, contentDescription = "Add", modifier = Modifier.size(28.dp)) },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = {
                BadgedBox(badge = { Badge { Text("5") } }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                }
            },
            selected = false, // Update with notification screen route
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.featured_image),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            },
            selected = currentDestination?.route == AppScreens.Profile.route,
            onClick = { 
                navController.navigate(AppScreens.Profile.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    FixUpTheme {
        BottomNavigationBar(rememberNavController())
    }
}

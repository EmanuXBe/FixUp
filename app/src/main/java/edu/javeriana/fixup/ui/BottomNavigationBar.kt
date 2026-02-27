package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.FixUpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = {
                BadgedBox(badge = { Badge { Text("5") } }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                }
            },
            selected = true,
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
            selected = false,
            onClick = { /*TODO*/ }
        )
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    FixUpTheme {
        BottomNavigationBar()
    }
}

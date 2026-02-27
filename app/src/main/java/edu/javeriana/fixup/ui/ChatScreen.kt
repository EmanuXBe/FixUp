
package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun ChatScreen() {

    Scaffold(
        bottomBar = { MessageInputBar() }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F4F4))
        ) {

            ChatTopBar()

            Spacer(modifier = Modifier.height(12.dp))

            ChatContent()
        }
    }
}

@Composable
fun ChatTopBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.pf1),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text("Andres Contreras", fontWeight = FontWeight.SemiBold)
                Text("Activo hace 11 minutos", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Outlined.Call, contentDescription = null)
            Icon(Icons.Outlined.Videocam, contentDescription = null)
        }
    }
}

@Composable
fun ChatContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        MessageBubble(
            text = "Hola! quisiera saber mas sobre tu solicitud",
            isMe = true
        )

        DateSeparator("30 de noviembre de 2023, 9:41 AM")

        MessageBubble("Hola", false)
        MessageBubble("Quisiera remodelar el piso de mi apartamento", false)
        MessageBubble("Cuando podrias pasar?", false)

        Spacer(modifier = Modifier.height(8.dp))

        MessageBubble("Claro! podria pasar el lunes", true)
        MessageBubble("Y asi hacer una cotizacion", true)

        Spacer(modifier = Modifier.height(8.dp))

        MessageBubble("Ok", false)
        MessageBubble("perfecto!", false)
        MessageBubble("el lunes estaria perfecto, estare atento", false)
    }
}

@Composable
fun MessageBubble(text: String, isMe: Boolean) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isMe) Color.Black else Color(0xFFE6E6E6)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (isMe) Color.White else Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DateSeparator(text: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun MessageInputBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF0F0F0)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Mensaje...",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Outlined.Mic, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Outlined.InsertEmoticon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Outlined.Image, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    FixUpTheme {
        ChatScreen()
    }
}

package com.example.aiagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIAssistantApp()
        }
    }
}

@Composable
fun AIAssistantApp() {
    var isSpeaking by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }

    // Pulsierender Kreis für den Agenten
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking) 1.35f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis: Int = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    // Neutraler, eleganter Farbverlauf für den Hintergrund
    val bgGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF111827), // Sehr dunkles Grau/Blau
            Color(0xFF1F2937), // Neutrales Slate
            Color(0xFF2563EB).copy(alpha = 0.25f), // Dezent Blau
            Color(0xFF7C3AED).copy(alpha = 0.15f)  // Dezent Violett
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // LINKER BEREICH: Der animierte KI-Agent
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Äußerer Puls-Ring
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFF6366F1),
                                    Color(0xFFA855F7)
                                )
                            )
                        )
                )
                // Innerer Kern des Agenten
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White.subduedAlpha())
                ) {
                    // Hier sitzt optisch der Agent
                }
            }

            // RECHTER BEREICH: Transkripte, Web-Medien (YouTube/Spotify Platzhalter)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Live-Transkript & Medien",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Warte auf Eingabe...\n(Hier erscheinen später Bilder, Web-Inhalte oder Musik-Player)",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // OBEN LINKS: Menü-Button für Einstellungen & Google-Login
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menü & Einstellungen",
                tint = Color.White
            )
        }
    }
}

// Hilfsfunktion für dezente Farben
fun Color.subduedAlpha(): Color = this.copy(alpha = 0.85f)
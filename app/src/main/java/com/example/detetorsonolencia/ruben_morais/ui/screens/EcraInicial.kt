package com.example.detetorsonolencia.ruben_morais.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EcraInicial(aoIniciarDeteccao: () -> Unit) {
    var visivel by remember { mutableStateOf(false) }
    val alfa by animateFloatAsState(
        targetValue = if (visivel) 1f else 0f,
        animationSpec = tween(800),
        label = "alfa"
    )

    LaunchedEffect(Unit) { visivel = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0E1A), Color(0xFF1A2035))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E3A5F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.RemoveRedEye,
                    contentDescription = null,
                    tint = Color(0xFF4FC3F7),
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Detetor de\nSonolência",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Monitorização em tempo real",
                fontSize = 15.sp,
                color = Color(0xFF4FC3F7),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            CartaoFuncionalidades(
                titulo = "Como funciona?",
                itens = listOf(
                    "📷  A câmara frontal monitoriza o teu rosto",
                    "👁️  O ML Kit analisa a probabilidade dos olhos estarem abertos",
                    "⏱️  Se os olhos ficarem fechados +2s, é emitido um alerta",
                    "🤕  O sistema deteta quando a cabeça cai e emite alerta",
                    "🔔  Vibração e som alertam-te imediatamente",
                    "💡  O sensor de luz deteta ambientes escuros"
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = aoIniciarDeteccao,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7)
                )
            ) {
                Text(
                    text = "Iniciar Deteção",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0A0E1A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Requer permissão de câmara",
                fontSize = 12.sp,
                color = Color(0xFF607D8B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CartaoFuncionalidades(titulo: String, itens: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2035))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            itens.forEach { item ->
                Text(
                    text = item,
                    fontSize = 14.sp,
                    color = Color(0xFFB0BEC5),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
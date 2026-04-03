package com.example.detetorsonolencia.ruben_morais.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.detetorsonolencia.ruben_morais.camera.AnalisadorRosto
import com.example.detetorsonolencia.ruben_morais.camera.GestorAlertas
import com.example.detetorsonolencia.ruben_morais.sensors.GestorSensores
import com.example.detetorsonolencia.ruben_morais.viewmodel.SonolenciaViewModel
import java.util.concurrent.Executors
import kotlin.math.abs

@Composable
fun EcraCamera(viewModel: SonolenciaViewModel, aoVoltar: () -> Unit) {
    val contexto = LocalContext.current
    val actividade = contexto as? ComponentActivity
    val estado by viewModel.estado.collectAsState()

    var temPermissaoCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val pedidoPermissaoCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida -> temPermissaoCamera = concedida }

    val gestorAlertas = remember { GestorAlertas(contexto) }

    val gestorSensores = remember {
        GestorSensores(
            contexto = contexto,
            aoAlterarLuz = { lux -> viewModel.aoAlterarSensorLuz(lux) },
            aoAlterarAcelerometro = { x, y, z -> viewModel.aoAlterarAcelerometro(x, y, z) }
        )
    }
    val temLuz = remember { gestorSensores.temSensorLuz() }
    val temAcelerometro = remember { gestorSensores.temAcelerometro() }

    // Ecrã sempre ligado
    DisposableEffect(Unit) {
        actividade?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            actividade?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(Unit) {
        if (!temPermissaoCamera) pedidoPermissaoCamera.launch(Manifest.permission.CAMERA)
        gestorSensores.iniciar()
    }

    DisposableEffect(Unit) {
        onDispose {
            gestorSensores.parar()
            gestorAlertas.pararAlerta()
        }
    }

    LaunchedEffect(estado.estaSonolento) {
        if (estado.estaSonolento) gestorAlertas.ativarAlerta() else gestorAlertas.pararAlerta()
    }

    if (!temPermissaoCamera) {
        EcraPermissaoNegada(
            aoVoltar = aoVoltar,
            aoPedirPermissao = { pedidoPermissaoCamera.launch(Manifest.permission.CAMERA) }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        PreviewCamera(
            aoDetectarRosto = { esq, dir, anguloX -> viewModel.aoDetectarRosto(esq, dir, anguloX) },
            aoNaoDetectarRosto = { viewModel.aoNaoDetectarRosto() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = aoVoltar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                text = "Monitorização Ativa",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xCC0A0E1A))
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CartaoEstado(estado.mensagemAlerta, estado.estaSonolento, estado.rostoDetectado, estado.cabecaInclinada)

            IndicadorAberturaOlhos(
                probEsquerdo = estado.probOlhoEsquerdo,
                probDireito = estado.probOlhoDireito,
                rostoDetectado = estado.rostoDetectado
            )

            if (estado.rostoDetectado) {
                IndicadorCabeca(
                    angulo = estado.anguloInclinacaoCabeca,
                    inclinada = estado.cabecaInclinada
                )
            }

            if (estado.duracaoOlhosFechadosMs > 0) {
                LinearProgressIndicator(
                    progress = { (estado.duracaoOlhosFechadosMs / 2000f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (estado.estaSonolento) Color(0xFFEF5350) else Color(0xFFFFA726),
                    trackColor = Color(0xFF37474F)
                )
            }

            FilaInfoSensores(
                luzBaixa = estado.luzBaixa,
                nivelLuz = estado.nivelLuz,
                emMovimento = estado.emMovimento,
                temSensorLuz = temLuz,
                temAcelerometro = temAcelerometro
            )
        }

        AnimatedVisibility(
            visible = estado.estaSonolento,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            AlertaSonolencia(
                cabecaInclinada = estado.cabecaInclinada,
                aoDispensar = { viewModel.dispensarAlerta() }
            )
        }
    }
}

@Composable
fun PreviewCamera(
    aoDetectarRosto: (Float, Float, Float) -> Unit,
    aoNaoDetectarRosto: () -> Unit
) {
    val proprietarioCicloVida = LocalLifecycleOwner.current
    val contexto = LocalContext.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            val vistaPreview = PreviewView(ctx)
            val futuroFornecedorCamera = ProcessCameraProvider.getInstance(ctx)

            futuroFornecedorCamera.addListener({
                val fornecedorCamera = futuroFornecedorCamera.get()

                val preview = Preview.Builder().build().apply {
                    surfaceProvider = vistaPreview.surfaceProvider
                }

                val analiseImagem = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor,
                            AnalisadorRosto(aoDetectarRosto, aoNaoDetectarRosto)
                        )
                    }

                try {
                    fornecedorCamera.unbindAll()
                    fornecedorCamera.bindToLifecycle(
                        proprietarioCicloVida,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        analiseImagem
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            vistaPreview
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun CartaoEstado(
    mensagem: String,
    estaSonolento: Boolean,
    rostoDetectado: Boolean,
    cabecaInclinada: Boolean
) {
    val corFundo by animateColorAsState(
        targetValue = when {
            estaSonolento -> Color(0x99EF5350)
            cabecaInclinada -> Color(0x99FF6F00)
            !rostoDetectado -> Color(0x99455A64)
            else -> Color(0x9943A047)
        },
        animationSpec = tween(300),
        label = "estado"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Text(
            text = if (mensagem.isEmpty()) "A iniciar..." else mensagem,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp).fillMaxWidth()
        )
    }
}

@Composable
private fun IndicadorAberturaOlhos(probEsquerdo: Float, probDireito: Float, rostoDetectado: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BarraOlho("Olho Esq.", probEsquerdo, rostoDetectado, Modifier.weight(1f))
        BarraOlho("Olho Dir.", probDireito, rostoDetectado, Modifier.weight(1f))
    }
}

@Composable
private fun BarraOlho(rotulo: String, prob: Float, rostoDetectado: Boolean, modifier: Modifier = Modifier) {
    val corBarra = when {
        !rostoDetectado -> Color(0xFF546E7A)
        prob > 0.7f -> Color(0xFF66BB6A)
        prob > 0.4f -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(rotulo, color = Color(0xFFB0BEC5), fontSize = 12.sp)
            Text(
                text = if (rostoDetectado) "${(prob * 100).toInt()}%" else "--",
                color = corBarra,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (rostoDetectado) prob else 0f },
            modifier = Modifier.fillMaxWidth(),
            color = corBarra,
            trackColor = Color(0xFF263238)
        )
    }
}

@Composable
private fun IndicadorCabeca(angulo: Float, inclinada: Boolean) {
    val cor = if (inclinada) Color(0xFFFF6F00) else Color(0xFF546E7A)
    val descricao = when {
        angulo < -20f -> "⚠️ Cabeça a cair (${angulo.toInt()}°)"
        angulo < -10f -> "↓ Cabeça ligeiramente inclinada (${angulo.toInt()}°)"
        abs(angulo) <= 10f -> "↕ Cabeça direita (${angulo.toInt()}°)"
        else -> "↑ Cabeça levantada (${angulo.toInt()}°)"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2035))
    ) {
        Text(
            text = descricao,
            color = cor,
            fontSize = 12.sp,
            fontWeight = if (inclinada) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun FilaInfoSensores(luzBaixa: Boolean, nivelLuz: Float, emMovimento: Boolean, temSensorLuz: Boolean, temAcelerometro: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (temSensorLuz) {
            ChipSensor(
                rotulo = if (luzBaixa) "⚠️ Luz baixa (${nivelLuz.toInt()} lux)" else "💡 ${nivelLuz.toInt()} lux",
                cor = if (luzBaixa) Color(0xFFFFA726) else Color(0xFF546E7A),
                modifier = Modifier.weight(1f)
            )
        } else {
            ChipSensor(
                rotulo = "💡 Sem sensor de luz",
                cor = Color(0xFF455A64),
                modifier = Modifier.weight(1f)
            )
        }

        if (temAcelerometro) {
            ChipSensor(
                rotulo = if (emMovimento) "🚗 Em movimento" else "🅿️ Parado",
                cor = if (emMovimento) Color(0xFFEF5350) else Color(0xFF546E7A),
                modifier = Modifier.weight(1f)
            )
        } else {
            ChipSensor(
                rotulo = "📡 Sem acelerómetro",
                cor = Color(0xFF455A64),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ChipSensor(rotulo: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2035))
    ) {
        Text(
            text = rotulo,
            color = cor,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlertaSonolencia(cabecaInclinada: Boolean, aoDispensar: () -> Unit) {
    val transicaoInfinita = rememberInfiniteTransition(label = "pulsar")
    val escala by transicaoInfinita.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "escala"
    )

    Card(
        modifier = Modifier.padding(24.dp).scale(escala),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (cabecaInclinada) "⚠️ Cabeça a Cair!" else "⚠️ Sonolência Detetada!",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (cabecaInclinada)
                    "A tua cabeça está a inclinar para a frente.\nSinal de microsono!"
                else
                    "Os teus olhos estiveram fechados\ndurante mais de 2 segundos.",
                color = Color(0xFFFFCDD2),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = aoDispensar,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Estou acordado!", color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EcraPermissaoNegada(aoVoltar: () -> Unit, aoPedirPermissao: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Permissão de câmara necessária", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Esta aplicação necessita de acesso à câmara para detetar sonolência.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = aoPedirPermissao) { Text("Conceder Permissão") }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = aoVoltar) { Text("Voltar") }
    }
}
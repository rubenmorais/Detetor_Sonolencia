package com.example.detetorsonolencia.ruben_morais.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EstadoSonolencia(
    val probOlhoEsquerdo: Float = 1f,
    val probOlhoDireito: Float = 1f,
    val rostoDetectado: Boolean = false,
    val estaSonolento: Boolean = false,
    val cabecaInclinada: Boolean = false,
    val anguloInclinacaoCabeca: Float = 0f,
    val mensagemAlerta: String = "",
    val duracaoOlhosFechadosMs: Long = 0L,
    val nivelLuz: Float = -1f,
    val luzBaixa: Boolean = false,
    val emMovimento: Boolean = true
)

class SonolenciaViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EstadoSonolencia())
    val estado: StateFlow<EstadoSonolencia> = _estado.asStateFlow()

    private var inicioOlhosFechados: Long? = null
    private val limiarSonolenciaMs = 2000L
    private val limiarOlhoFechado = 0.4f
    // Cabeça inclinada para a frente mais de 20° é sinal de microsono
    private val limiarInclinacaoCabeca = 20f

    fun aoDetectarRosto(probOlhoEsq: Float, probOlhoDir: Float, anguloX: Float) {
        val probMediaOlhos = (probOlhoEsq + probOlhoDir) / 2f
        val olhosFechados = probMediaOlhos < limiarOlhoFechado
        val cabecaInclinada = anguloX < -limiarInclinacaoCabeca
        val agora = System.currentTimeMillis()

        if (olhosFechados || cabecaInclinada) {
            if (inicioOlhosFechados == null) {
                inicioOlhosFechados = agora
            }
            val duracaoFechados = agora - (inicioOlhosFechados ?: agora)
            val estaSonolento = duracaoFechados >= limiarSonolenciaMs

            val mensagem = when {
                estaSonolento && cabecaInclinada -> "Cabeça a cair! Sonolência detetada!"
                estaSonolento -> "Sonolência detetada!"
                cabecaInclinada -> "Cabeça a inclinar..."
                else -> "Olhos fechados..."
            }

            _estado.update {
                it.copy(
                    probOlhoEsquerdo = probOlhoEsq,
                    probOlhoDireito = probOlhoDir,
                    rostoDetectado = true,
                    estaSonolento = estaSonolento,
                    cabecaInclinada = cabecaInclinada,
                    anguloInclinacaoCabeca = anguloX,
                    duracaoOlhosFechadosMs = duracaoFechados,
                    mensagemAlerta = mensagem
                )
            }
        } else {
            inicioOlhosFechados = null
            _estado.update {
                it.copy(
                    probOlhoEsquerdo = probOlhoEsq,
                    probOlhoDireito = probOlhoDir,
                    rostoDetectado = true,
                    estaSonolento = false,
                    cabecaInclinada = false,
                    anguloInclinacaoCabeca = anguloX,
                    duracaoOlhosFechadosMs = 0L,
                    mensagemAlerta = "Atento"
                )
            }
        }
    }

    fun aoNaoDetectarRosto() {
        inicioOlhosFechados = null
        _estado.update {
            it.copy(
                rostoDetectado = false,
                estaSonolento = false,
                cabecaInclinada = false,
                anguloInclinacaoCabeca = 0f,
                duracaoOlhosFechadosMs = 0L,
                mensagemAlerta = "Nenhum rosto detetado"
            )
        }
    }

    fun aoAlterarSensorLuz(lux: Float) {
        val luzBaixa = lux < 50f
        _estado.update { it.copy(nivelLuz = lux, luzBaixa = luzBaixa) }
    }

    fun aoAlterarAcelerometro(x: Float, y: Float, z: Float) {
        val aceleracao = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        _estado.update { it.copy(emMovimento = aceleracao > 10.5f) }
    }

    fun dispensarAlerta() {
        inicioOlhosFechados = null
        _estado.update { it.copy(estaSonolento = false, duracaoOlhosFechadosMs = 0L) }
    }
}
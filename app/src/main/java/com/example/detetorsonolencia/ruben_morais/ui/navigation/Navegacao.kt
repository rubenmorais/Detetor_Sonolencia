package com.example.detetorsonolencia.ruben_morais.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.detetorsonolencia.ruben_morais.ui.screens.EcraCamera
import com.example.detetorsonolencia.ruben_morais.ui.screens.EcraInicial
import com.example.detetorsonolencia.ruben_morais.viewmodel.SonolenciaViewModel

object Rotas {
    const val INICIO = "inicio"
    const val CAMERA = "camera"
}

@Composable
fun NavegacaoApp() {
    val controladorNav = rememberNavController()
    val viewModel: SonolenciaViewModel = viewModel()

    NavHost(navController = controladorNav, startDestination = Rotas.INICIO) {
        composable(Rotas.INICIO) {
            EcraInicial(aoIniciarDeteccao = { controladorNav.navigate(Rotas.CAMERA) })
        }
        composable(Rotas.CAMERA) {
            EcraCamera(
                viewModel = viewModel,
                aoVoltar = { controladorNav.popBackStack() }
            )
        }
    }
}
package com.example.detetorsonolencia.ruben_morais

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.detetorsonolencia.ruben_morais.ui.navigation.NavegacaoApp
import com.example.detetorsonolencia.ruben_morais.ui.theme.DetetorSonolenciaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DetetorSonolenciaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavegacaoApp()
                }
            }
        }
    }
}
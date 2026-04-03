package com.example.detetorsonolencia.ruben_morais.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class AnalisadorRosto(
    private val aoDetectarRosto: (olhoEsq: Float, olhoDir: Float, anguloX: Float) -> Unit,
    private val aoNaoDetectarRosto: () -> Unit
) : ImageAnalysis.Analyzer {

    private val opcoes = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .setMinFaceSize(0.15f)
        .build()

    private val detector = FaceDetection.getClient(opcoes)

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imagemProxy: ImageProxy) {
        val imagemMedia = imagemProxy.image ?: run {
            imagemProxy.close()
            return
        }

        val imagem = InputImage.fromMediaImage(imagemMedia, imagemProxy.imageInfo.rotationDegrees)

        detector.process(imagem)
            .addOnSuccessListener { rostos ->
                if (rostos.isEmpty()) {
                    aoNaoDetectarRosto()
                } else {
                    val rosto = rostos.first()
                    val olhoEsq = rosto.rightEyeOpenProbability ?: 1f
                    val olhoDir = rosto.leftEyeOpenProbability ?: 1f
                    val anguloX = rosto.headEulerAngleX
                    aoDetectarRosto(olhoEsq, olhoDir, anguloX)
                }
            }
            .addOnFailureListener { aoNaoDetectarRosto() }
            .addOnCompleteListener { imagemProxy.close() }
    }
}

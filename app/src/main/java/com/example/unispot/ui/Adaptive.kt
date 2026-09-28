package com.example.unispot.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Clasificación del ancho de la ventana con los mismos cortes que usan las
 * Window Size Classes de Material: compacta por debajo de 600dp, mediana hasta
 * 840dp y expandida a partir de ahí. Se calcula a mano para no agregar otra
 * dependencia solo por esto.
 */
enum class AnchoVentana {
    COMPACTA,
    MEDIANA,
    EXPANDIDA;

    val esCompacta: Boolean get() = this == COMPACTA
    val esExpandida: Boolean get() = this == EXPANDIDA
    val esMedianaOMayor: Boolean get() = this != COMPACTA
}

@Composable
@ReadOnlyComposable
fun anchoDeVentana(): AnchoVentana = clasificarAncho(LocalConfiguration.current.screenWidthDp)

/**
 * Clasificación a partir del ancho en dp. Va aparte de la versión @Composable
 * para poder probarla sin montar la interfaz.
 */
fun clasificarAncho(anchoDp: Int): AnchoVentana = when {
    anchoDp < 600 -> AnchoVentana.COMPACTA
    anchoDp < 840 -> AnchoVentana.MEDIANA
    else -> AnchoVentana.EXPANDIDA
}

@Composable
@ReadOnlyComposable
fun esPantallaExpandida(): Boolean = anchoDeVentana().esExpandida

/**
 * Acota el ancho del contenido y lo centra.
 *
 * Sin esto, en una tablet los formularios se estiran de borde a borde y las
 * líneas quedan larguísimas: una línea de texto no debería pasar de unos 80
 * caracteres. Se usa un contenedor Box porque `widthIn` por sí solo deja el
 * contenido pegado a la izquierda en lugar de centrarlo.
 */
@Composable
fun ContenidoCentrado(
    anchoMaximo: Dp = 520.dp,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = anchoMaximo)
                .fillMaxHeight()
        ) {
            contenido()
        }
    }
}

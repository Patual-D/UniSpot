package com.example.unispot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colores propios de UniSpot que no encajan en el colorScheme de Material3,
 * porque el contenido que va sobre los verdes es siempre el mismo (un verde
 * muy oscuro) tanto en tema claro como en oscuro.
 */
data class UniSpotColors(
    val contenidoSobreVerde: Color,
    val contenidoSobreVerdeClaro: Color,
    val contenidoSobreVerdeOscuro: Color,
    val celdaLibre: Color,
    val bordeCelda: Color,
    val textoSecundario: Color,
    val columnaHoy: Color,
    val verdePrincipal: Color,
    val verdeClaro: Color,
    val verdeOscuro: Color,
    val morado: Color
)

private val UniSpotColores = UniSpotColors(
    contenidoSobreVerde = ContenidoSobreVerdePrincipal,
    contenidoSobreVerdeClaro = ContenidoSobreVerdeClaro,
    contenidoSobreVerdeOscuro = ContenidoSobreVerdeOscuro,
    celdaLibre = CeldaLibre,
    bordeCelda = BordeCelda,
    textoSecundario = TextoSecundario,
    columnaHoy = Color(0xFFDDDDDD),
    verdePrincipal = VerdePrincipal,
    verdeClaro = VerdeClaro,
    verdeOscuro = VerdeOscuro,
    morado = Morado
)

private val UniSpotColoresOscuros = UniSpotColors(
    contenidoSobreVerde = ContenidoSobreVerdePrincipal,
    contenidoSobreVerdeClaro = ContenidoSobreVerdeClaro,
    contenidoSobreVerdeOscuro = ContenidoSobreVerdeOscuro,
    celdaLibre = CeldaLibreOscura,
    bordeCelda = BordeCeldaOscuro,
    textoSecundario = TextoSecundarioOscuro,
    columnaHoy = Color(0xFF3A3A3A),
    verdePrincipal = VerdePrincipal,
    verdeClaro = VerdeClaro,
    verdeOscuro = VerdeOscuro,
    morado = Morado
)

val LocalUniSpotColors = staticCompositionLocalOf { UniSpotColores }

private val EsquemaClaro = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = ContenidoSobreVerdePrincipal,
    secondary = VerdeOscuro,
    onSecondary = ContenidoSobreVerdeOscuro,
    tertiary = Morado,
    onTertiary = Color.White,
    background = Fondo,
    onBackground = VerdeOscuro,
    surface = Color.White,
    onSurface = VerdeOscuro,
    surfaceVariant = Color(0xFFE2E2E2),
    onSurfaceVariant = TextoSecundario,
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = ContenidoSobreVerdeClaro,
    secondary = VerdeOscuro,
    onSecondary = ContenidoSobreVerdeOscuro,
    tertiary = Morado,
    onTertiary = Color.White,
    background = FondoOscuro,
    onBackground = VerdeClaro,
    surface = SuperficieOscura,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = SuperficieOscuraAlta,
    onSurfaceVariant = TextoSecundarioOscuro,
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun UniSpotTheme(
    temaOscuro: Boolean,
    contenido: @Composable () -> Unit
) {
    val esquema = if (temaOscuro) EsquemaOscuro else EsquemaClaro
    val colores = if (temaOscuro) UniSpotColoresOscuros else UniSpotColores

    CompositionLocalProvider(LocalUniSpotColors provides colores) {
        MaterialTheme(
            colorScheme = esquema,
            typography = Typography(),
            content = contenido
        )
    }
}

/** Acceso corto a los colores de UniSpot: `UniSpotTheme.colors.verdePrincipal`. */
object UniSpotTheme {
    val colors: UniSpotColors
        @Composable
        @ReadOnlyComposable
        get() = LocalUniSpotColors.current
}

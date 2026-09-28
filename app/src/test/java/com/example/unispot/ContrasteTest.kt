package com.example.unispot

import androidx.compose.ui.graphics.Color
import com.example.unispot.ui.AnchoVentana
import com.example.unispot.ui.clasificarAncho
import com.example.unispot.ui.theme.BordeCelda
import com.example.unispot.ui.theme.CeldaLibre
import com.example.unispot.ui.theme.CeldaReservada
import com.example.unispot.ui.theme.ContenidoSobreVerdeClaro
import com.example.unispot.ui.theme.ContenidoSobreVerdeOscuro
import com.example.unispot.ui.theme.ContenidoSobreVerdePrincipal
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.Morado
import com.example.unispot.ui.theme.TextoSecundario
import com.example.unispot.ui.theme.VerdeClaro
import com.example.unispot.ui.theme.VerdeOscuro
import com.example.unispot.ui.theme.VerdePrincipal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El proyecto no puede tocar los verdes de marca, así que el contraste se
 * resuelve por el lado del texto. Estos tests existen para que un cambio futuro
 * de color no rompa la accesibilidad en silencio: si alguien ajusta un verde y
 * vuelve a poner el texto en blanco, la suite falla.
 */
class ContrasteTest {

    /** WCAG 2.1, fórmula de contraste relativo. */
    private fun contraste(uno: Color, otro: Color): Double {
        val a = luminancia(uno)
        val b = luminancia(otro)
        val claro = maxOf(a, b)
        val oscuro = minOf(a, b)
        return (claro + 0.05) / (oscuro + 0.05)
    }

    private fun luminancia(color: Color): Double {
        fun canal(valor: Float): Double {
            val s = valor.toDouble()
            return if (s <= 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
        }

        return 0.2126 * canal(color.red) +
            0.7152 * canal(color.green) +
            0.0722 * canal(color.blue)
    }

    private fun assertAA(uno: Color, otro: Color, contexto: String) {
        val ratio = contraste(uno, otro)
        assertTrue(
            "$contexto: contraste $ratio, se necesita 4.5",
            ratio >= 4.5
        )
    }

    /** 3:1 es lo que se le pide a un componente de interfaz, no a un texto. */
    private fun assertUI(uno: Color, otro: Color, contexto: String) {
        val ratio = contraste(uno, otro)
        assertTrue("$contexto: contraste $ratio, se necesita 3.0", ratio >= 3.0)
    }

    @Test
    fun `los verdes de marca se mantienen intactos`() {
        // Es una decisión de diseño explícita: no se pueden cambiar.
        assertEquals(Color(0xFF00B85A), VerdePrincipal)
        assertEquals(Color(0xFF18D58A), VerdeClaro)
        assertEquals(Color(0xFF00605A), VerdeOscuro)
    }

    @Test
    fun `el texto sobre el verde claro cumple AA`() {
        // El blanco aquí daba 1.92:1, ilegible.
        assertAA(ContenidoSobreVerdeClaro, VerdeClaro, "texto sobre verde claro")
    }

    @Test
    fun `el texto sobre el verde principal cumple AA`() {
        // El blanco aquí daba 2.62:1.
        assertAA(ContenidoSobreVerdePrincipal, VerdePrincipal, "texto sobre verde principal")
    }

    @Test
    fun `el texto sobre el verde oscuro cumple AA`() {
        assertAA(ContenidoSobreVerdeOscuro, VerdeOscuro, "texto sobre verde oscuro")
    }

    @Test
    fun `el texto secundario sobre el fondo cumple AA`() {
        assertAA(TextoSecundario, Fondo, "texto secundario")
    }

    @Test
    fun `el morado conserva el texto blanco`() {
        // El verde oscuro de contenido no sirve sobre morado: ahí se queda
        // blanco, que sí da 15.5:1.
        assertAA(Color.White, Morado, "texto blanco sobre morado")
    }

    @Test
    fun `las celdas libres y reservadas se distinguen entre si`() {
        assertUI(CeldaLibre, CeldaReservada, "celda libre contra celda reservada")
    }

    @Test
    fun `el borde de la celda la separa del fondo`() {
        // El verde claro sobre el fondo gris queda en 1.66:1: sin borde, las
        // celdas libres se perderían en el fondo.
        assertUI(BordeCelda, Fondo, "borde de celda contra el fondo")
    }

    @Test
    fun `el borde de la celda se ve sobre el relleno de la celda`() {
        assertUI(BordeCelda, CeldaLibre, "borde de celda contra relleno verde claro")
    }
}

class ClasificacionDePantallaTest {

    @Test
    fun `un movil vertical es compacto`() {
        assertEquals(AnchoVentana.COMPACTA, clasificarAncho(360))
        assertEquals(AnchoVentana.COMPACTA, clasificarAncho(599))
    }

    @Test
    fun `una tablet vertical es mediana`() {
        assertEquals(AnchoVentana.MEDIANA, clasificarAncho(600))
        assertEquals(AnchoVentana.MEDIANA, clasificarAncho(839))
    }

    @Test
    fun `una tablet horizontal o plegable abierto es expandida`() {
        assertEquals(AnchoVentana.EXPANDIDA, clasificarAncho(840))
        assertEquals(AnchoVentana.EXPANDIDA, clasificarAncho(1280))
    }

    @Test
    fun `solo la expandida usa los dos paneles`() {
        assertTrue(clasificarAncho(1280).esExpandida)
        assertTrue(!clasificarAncho(800).esExpandida)
        assertTrue(!clasificarAncho(411).esExpandida)
    }

    @Test
    fun `a partir de mediana se cambia la barra inferior por la lateral`() {
        assertTrue(clasificarAncho(600).esMedianaOMayor)
        assertTrue(clasificarAncho(840).esMedianaOMayor)
        assertTrue(!clasificarAncho(599).esMedianaOMayor)
    }
}

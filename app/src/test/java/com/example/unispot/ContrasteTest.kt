package com.example.unispot

import androidx.compose.ui.graphics.Color
import com.example.unispot.data.CategoriaReserva
import com.example.unispot.ui.AnchoVentana
import com.example.unispot.ui.clasificarAncho
import com.example.unispot.ui.theme.BordeCelda
import com.example.unispot.ui.theme.CeldaLibre
import com.example.unispot.ui.theme.ContenidoSobreVerdeClaro
import com.example.unispot.ui.theme.ContenidoSobreVerdeOscuro
import com.example.unispot.ui.theme.ContenidoSobreVerdePrincipal
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.Morado
import com.example.unispot.ui.theme.TextoSecundario
import com.example.unispot.ui.theme.VerdeClaro
import com.example.unispot.ui.theme.VerdeOscuro
import com.example.unispot.ui.theme.VerdePrincipal
import com.example.unispot.ui.theme.color
import com.example.unispot.ui.theme.colorBorde
import com.example.unispot.ui.theme.colorContenido
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
    fun `el borde de la celda la separa del fondo`() {
        // El relleno neutro queda a 1.08:1 del fondo gris: sin borde, las
        // celdas libres se perderían en el fondo.
        assertUI(BordeCelda, Fondo, "borde de celda contra el fondo")
    }

    @Test
    fun `el borde de la celda se ve sobre el relleno de la celda`() {
        assertUI(BordeCelda, CeldaLibre, "borde de celda contra relleno neutro claro")
    }

    @Test
    fun `el texto de cada categoria cumple AA sobre su color`() {
        // Cuatro de las cinco categorías son pasteles: con texto blanco bajaban
        // a 1.4:1 - 2.02:1. "Juntas" es la única oscura y sí lleva blanco.
        CategoriaReserva.entries.forEach { categoria ->
            assertAA(
                categoria.colorContenido,
                categoria.color,
                "texto de ${categoria.nombre}"
            )
        }
    }

    @Test
    fun `el borde de cada categoria recorta su celda`() {
        // WCAG 1.4.11: el límite de un componente necesita 3:1 contra su propio
        // relleno. Aquí es lo único que separa dos celdas vecinas, porque los
        // cinco rellenos se parecen demasiado entre sí.
        CategoriaReserva.entries.forEach { categoria ->
            assertUI(
                categoria.colorBorde,
                categoria.color,
                "borde de ${categoria.nombre} contra su relleno"
            )
        }
    }

    @Test
    fun `cada celda se recorta contra el fondo de la pantalla`() {
        // Una celda puede apoyarse en su relleno o en su borde para separarse
        // del fondo, y no siempre en los dos. Las cuatro categorías claras
        // tienen un relleno que apenas contrasta con el fondo gris (1.2:1 a
        // 1.9:1) pero un borde que sí llega (5.43:1 a 8.91:1). "Juntas" es al
        // revés: su borde claro se pierde contra el fondo, pero su relleno
        // oscuro da 12.75:1. Exigir las dos cosas a la vez rechazaría un diseño
        // que en la práctica sí se ve bien.
        val celdas = listOf(
            Triple("Libre", CeldaLibre, BordeCelda)
        ) + CategoriaReserva.entries.map { Triple(it.nombre, it.color, it.colorBorde) }

        celdas.forEach { (nombre, relleno, borde) ->
            val porRelleno = contraste(relleno, Fondo)
            val porBorde = contraste(borde, Fondo)
            assertTrue(
                "$nombre no se distingue del fondo: relleno $porRelleno, borde $porBorde, " +
                    "y hace falta que alguno llegue a 3.0",
                maxOf(porRelleno, porBorde) >= 3.0
            )
        }
    }

    @Test
    fun `el limite entre dos celdas vecinas siempre queda marcado`() {
        // Este es el test que justifica el diseño. Dos rellenos contiguos casi
        // nunca llegan a 3:1 entre sí (Talleres contra Punto de Encuentro daba
        // 1.03:1), así que comparar relleno contra relleno no sirve. Lo que hace
        // legible la rejilla es que el contorno de una de las dos celdas se
        // distinga del relleno de la otra; si los rellenos ya se distinguen
        // solos, como pasa con las celdas oscuras, tampoco hace falta borde.
        //
        // Se recorren todos los pares, incluida la celda libre, y se exige que
        // alguno de los tres contraste alcance 3:1.
        val celdas = listOf(
            Triple("Libre", CeldaLibre, BordeCelda)
        ) + CategoriaReserva.entries.map { Triple(it.nombre, it.color, it.colorBorde) }

        for (i in celdas.indices) {
            for (j in i + 1 until celdas.size) {
                val (nombreA, rellenoA, bordeA) = celdas[i]
                val (nombreB, rellenoB, bordeB) = celdas[j]

                val mejor = listOf(
                    contraste(rellenoA, rellenoB) to "los rellenos",
                    contraste(bordeA, rellenoB) to "el borde de $nombreA",
                    contraste(bordeB, rellenoA) to "el borde de $nombreB"
                ).maxBy { it.first }

                assertTrue(
                    "El límite entre $nombreA y $nombreB no se marca: el mejor " +
                        "contraste es ${mejor.first} (${mejor.second}) y se necesita 3.0",
                    mejor.first >= 3.0
                )
            }
        }
    }

    @Test
    fun `la celda libre es neutra y no un color de marca`() {
        // Los cinco colores de categoría estaban entre 1.02:1 y 1.37:1 contra
        // el verde de marca, así que con la libre en ese verde una celda de
        // Talleres y una libre eran indistinguibles. La libre pasó a gris
        // neutro, y por eso sus tres canales tienen que ser iguales: es lo que
        // garantiza que no se parezca a ninguna categoría.
        assertEquals(CeldaLibre.red, CeldaLibre.green)
        assertEquals(CeldaLibre.green, CeldaLibre.blue)
        assertTrue(
            "la celda libre debe seguir lejos del verde de marca",
            contraste(CeldaLibre, VerdeClaro) < 3.0
        )
    }

    @Test
    fun `cada categoria tiene un nombre y una clave distintos`() {
        val claves = CategoriaReserva.entries.map { it.clave }
        val nombres = CategoriaReserva.entries.map { it.nombre }
        assertEquals(claves.size, claves.toSet().size)
        assertEquals(nombres.size, nombres.toSet().size)
    }
}

class CategoriaReservaTest {

    @Test
    fun `una clave desconocida cae en la categoria por defecto`() {
        // Si mañana se agrega una categoría y queda una vieja en la base, la
        // reserva debe seguir siendo visible en vez de romper la pantalla.
        assertEquals(CategoriaReserva.POR_DEFECTO, CategoriaReserva.desdeClave("NO_EXISTE"))
        assertEquals(CategoriaReserva.POR_DEFECTO, CategoriaReserva.desdeClave(null))
    }

    @Test
    fun `cada clave guardada se vuelve a leer como su categoria`() {
        CategoriaReserva.entries.forEach { categoria ->
            assertEquals(categoria, CategoriaReserva.desdeClave(categoria.clave))
        }
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

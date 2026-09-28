package com.example.unispot.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.unispot.data.CategoriaReserva

/**
 * Colores de las categorías de reserva.
 *
 * Cada categoría trae tres colores porque los cinco tonos elegidos están en el
 * mismo rango de luminosidad: cuatro son pasteles claros y solo "Juntas" es
 * oscuro. Eso obliga a resolver el texto y el borde por categoría en vez de
 * con un par de valores globales.
 *
 * - [color] es el relleno de la celda.
 * - [colorBorde] recorta la celda. Siempre alcanza al menos 3:1 contra su
 *   propio relleno, que es lo que pide WCAG 1.4.11 para el límite de un
 *   componente. En las cuatro categorías claras el borde es una variante
 *   oscura del relleno; en "Juntas", cuyo relleno ya es muy oscuro, tiene que
 *   ser claro (oscurecerlo más daba 1.65:1 y el borde desaparecía).
 * - [colorContenido] es el texto y los iconos que van dentro de la celda, y
 *   está medido para llegar a 4.5:1.
 *
 * Ojo con un detalle que no tiene arreglo por color: cuatro rellenos contiguos
 * no alcanzan el 3:1 entre sí (Talleres contra Punto de Encuentro da 1.03:1).
 * Por eso el nombre de la categoría aparece además en la leyenda, en el
 * `contentDescription` de la celda y en el modal. WCAG 1.4.1 prohibe que el
 * color sea la única pista, y aquí además el color por sí solo no alcanza.
 */
private data class EstiloCategoria(
    val color: Color,
    val colorBorde: Color,
    val colorContenido: Color
)

private val Estilos = mapOf(
    CategoriaReserva.CLASES to EstiloCategoria(
        color = Color(0xFF92D6AC),
        colorBorde = Color(0xFF2F6B4F),        // 3.73:1 contra su relleno
        colorContenido = Color(0xFF00261D)     // 9.60:1
    ),
    CategoriaReserva.TALLERES to EstiloCategoria(
        color = Color(0xFF26D07C),
        colorBorde = Color(0xFF0B4A2F),        // 5.11:1
        colorContenido = Color(0xFF00261D)     // 8.03:1
    ),
    CategoriaReserva.PUNTO_DE_ENCUENTRO to EstiloCategoria(
        color = Color(0xFF11CBE7),
        colorBorde = Color(0xFF0A5E6E),        // 3.78:1
        colorContenido = Color(0xFF00261D)     // 8.28:1
    ),
    CategoriaReserva.JUNTAS_Y_CONFERENCIAS to EstiloCategoria(
        color = Color(0xFF00534C),
        colorBorde = Color(0xFF4FB8AA),        // 3.74:1, y este sí es claro
        colorContenido = Color(0xFFFFFFFF)     // 8.97:1
    ),
    CategoriaReserva.ZONA_ESTUDIANTIL to EstiloCategoria(
        color = Color(0xFF5AF1D8),
        colorBorde = Color(0xFF0A5C5A),        // 5.58:1
        colorContenido = Color(0xFF00261D)     // 11.59:1
    )
)

/** Relleno de la celda para una categoría. */
val CategoriaReserva.color: Color
    get() = Estilos.getValue(this).color

/** Contorno de la celda, el que la separa de sus vecinas. */
val CategoriaReserva.colorBorde: Color
    get() = Estilos.getValue(this).colorBorde

/** Texto e iconos dentro de una celda de esta categoría. */
val CategoriaReserva.colorContenido: Color
    get() = Estilos.getValue(this).colorContenido

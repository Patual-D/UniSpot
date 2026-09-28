package com.example.unispot.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de marca. Estos verdes no se modifican.
val VerdePrincipal = Color(0xFF00B85A)
val VerdeClaro = Color(0xFF18D58A)
val VerdeOscuro = Color(0xFF00605A)
val Morado = Color(0xFF24165F)
val Fondo = Color(0xFFEFEEEE)

// Fondos del tema oscuro.
val FondoOscuro = Color(0xFF121212)
val SuperficieOscura = Color(0xFF1B1B1B)
val SuperficieOscuraAlta = Color(0xFF2A2A2A)

/**
 * Contenido (texto e iconos) que va sobre los verdes de marca.
 *
 * Los verdes se mantienen intactos, así que el contraste se resuelve por el
 * lado del texto: el blanco sobre VerdeClaro daba 1.92:1 y el blanco sobre
 * VerdePrincipal daba 2.62:1, ambos muy por debajo del 4.5:1 que exige WCAG AA.
 * Con estos verdes muy oscuros el resultado es 8.44:1 y 6.18:1.
 */
val ContenidoSobreVerdeClaro = Color(0xFF00261D)
val ContenidoSobreVerdePrincipal = Color(0xFF00261D)
val ContenidoSobreVerdeOscuro = Color(0xFFFFFFFF)

// Colores del calendario.
//
// La celda libre dejó de usar el verde de marca. Los cinco colores de categoría
// están todos en el mismo rango de luminosidad que VerdeClaro, así que con la
// libre en verde los pares quedaban entre 1.02:1 y 1.37:1: una celda de
// Talleres y una libre se veían prácticamente igual. Ahora la libre es un
// neutro apagado y el color queda para las celdas con reserva, que son las que
// deben destacar.
val CeldaLibre = Color(0xFFF7F7F7)
val CeldaLibreOscura = Color(0xFF2A2A2A)

// El relleno neutro queda a 1.08:1 del fondo, así que sin borde la celda se
// perdería. El borde es lo que la recorta: 4.01:1 contra su propio relleno en
// tema claro y 5.1:1 en oscuro.
val BordeCelda = Color(0xFF7A7A7A)
val BordeCeldaOscuro = Color(0xFF9A9A9A)

// Texto secundario: el gris por defecto daba 3.41:1, insuficiente para texto
// normal. Este valor alcanza 7.65:1 sobre el fondo claro.
val TextoSecundario = Color(0xFF4A4A4A)
val TextoSecundarioOscuro = Color(0xFFC2C2C2)

// Ojo: ContenidoSobreVerde NO sirve sobre Morado (1.05:1). Los botones morados
// se quedan con texto blanco, que ahí sí da 15.5:1.

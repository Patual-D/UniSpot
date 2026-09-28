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
// Las celdas libres usan un único color plano; solo las reservadas cambian.
// Entre ambos verdes hay 3.87:1, por encima del 3:1 que se pide a un
// componente de interfaz para poder distinguirlo del que tiene al lado.
val CeldaLibre = VerdeClaro
val CeldaReservada = VerdeOscuro
val ContenidoCeldaReservada = Color(0xFFFFFFFF)

// El verde claro sobre el fondo gris queda en 1.66:1, así que las celdas
// llevan un borde propio: es lo que las separa del fondo (6.07:1) y da el
// contorno que necesita quien tenga baja visión.
val BordeCelda = Color(0xFF0B6640)
val BordeCeldaOscuro = Color(0xFF1E5C3A)

// Texto secundario: el gris por defecto daba 3.41:1, insuficiente para texto
// normal. Este valor alcanza 7.65:1 sobre el fondo claro.
val TextoSecundario = Color(0xFF4A4A4A)
val TextoSecundarioOscuro = Color(0xFFC2C2C2)

// Ojo: ContenidoSobreVerde NO sirve sobre Morado (1.05:1). Los botones morados
// se quedan con texto blanco, que ahí sí da 15.5:1.

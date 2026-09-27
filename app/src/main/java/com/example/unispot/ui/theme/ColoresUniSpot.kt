package com.example.unispot.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de la aplicación.
val VerdePrincipal = Color(0xFF00B85A)
val VerdeClaro = Color(0xFF18D58A)
val VerdeOscuro = Color(0xFF00605A)
val Morado = Color(0xFF24165F)
val Fondo = Color(0xFFEFEEEE)

// Colores del calendario.
// Las celdas libres usan un único color plano; solo las reservadas cambian a
// VerdeOscuro. El borde es apenas un tono del propio verde, no un color nuevo.
val CeldaLibre = VerdeClaro
val CeldaReservada = VerdeOscuro
val CeldaBorde = Color(0xFF0E9E62)
val ColumnaHoy = Color(0xFFDDDDDD)

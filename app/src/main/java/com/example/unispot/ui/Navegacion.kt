package com.example.unispot.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.unispot.data.AulaEntity
import com.example.unispot.data.EdificioEntity

enum class Pantalla {
    LOGIN,
    REGISTRO,
    EDIFICIOS,
    SALONES,
    HORARIO,
    RESERVAR,
    RESERVAS,
    PERFIL
}

/**
 * Estado de navegación. Antes de esto los edificios y salones se pasaban por
 * lambdas que descartaban lo que el usuario había tocado, así que las pantallas
 * siguientes mostraban siempre "Aula 101" fijo. Aquí se guarda qué edificio y
 * qué aula se eligieron de verdad.
 */
class ControladorNavegacion {

    var pantalla by mutableStateOf(Pantalla.EDIFICIOS)
        private set

    var edificio by mutableStateOf<EdificioEntity?>(null)
        private set

    var aula by mutableStateOf<AulaEntity?>(null)
        private set

    /** Datos que llegan del calendario al formulario de reserva. */
    var reservaPendiente by mutableStateOf<ReservaPendiente?>(null)
        private set

    fun irA(destino: Pantalla) {
        when (destino) {
            Pantalla.EDIFICIOS -> {
                edificio = null
                aula = null
            }
            Pantalla.SALONES -> {
                aula = null
            }
            Pantalla.HORARIO -> {
                reservaPendiente = null
            }
            else -> Unit
        }
        pantalla = destino
    }

    fun elegirEdificio(edificio: EdificioEntity) {
        this.edificio = edificio
        pantalla = Pantalla.SALONES
    }

    fun elegirAula(aula: AulaEntity) {
        this.aula = aula
        pantalla = Pantalla.HORARIO
    }

    fun prepararReserva(pendiente: ReservaPendiente) {
        reservaPendiente = pendiente
        pantalla = Pantalla.RESERVAR
    }

    fun limpiarPendiente() {
        reservaPendiente = null
    }

    fun puedeVolver(): Boolean = when (pantalla) {
        Pantalla.EDIFICIOS, Pantalla.LOGIN, Pantalla.REGISTRO -> false
        else -> true
    }

    /** Devuelve false si ya se está en la raíz, para dejar pasar el gesto. */
    fun volver(): Boolean {
        if (!puedeVolver()) return false

        pantalla = when (pantalla) {
            Pantalla.SALONES -> Pantalla.EDIFICIOS
            Pantalla.HORARIO -> Pantalla.SALONES
            Pantalla.RESERVAR -> Pantalla.HORARIO
            Pantalla.RESERVAS, Pantalla.PERFIL -> Pantalla.EDIFICIOS
            else -> Pantalla.EDIFICIOS
        }
        return true
    }
}

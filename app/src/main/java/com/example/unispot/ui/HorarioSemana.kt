package com.example.unispot.ui

import com.example.unispot.data.ReservacionEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

val EsPanol = Locale.forLanguageTag("es-MX")

/**
 * Modelo de la rejilla semanal: 07:00 a 21:00 en bloques de una hora, de lunes
 * a viernes. Centraliza el cálculo de qué reservas ocupa cada bloque para que
 * la pantalla no tenga que hacerlo celda por celda.
 */
object HorarioSemana {

    const val HORA_INICIO = 7
    const val HORA_FIN = 21
    const val NUM_BLOQUES = HORA_FIN - HORA_INICIO

    private val DIAS = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    val ENCABEZADOS = DIAS.map { it.getDisplayName(TextStyle.SHORT, EsPanol).replace(".", "") }

    /** Lunes de la semana a la que pertenece cualquier fecha. */
    fun lunesDe(fecha: LocalDate): LocalDate =
        fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun diasDeLunes(lunes: LocalDate): List<LocalDate> = DIAS.map { lunes.plusDays(it.value - 1L) }

    fun inicioDeBloque(indice: Int): LocalTime = LocalTime.of(HORA_INICIO + indice, 0)

    fun etiquetaHora(indice: Int): String = "%02d:00".format(HORA_INICIO + indice)

    /**
     * Agrupa las reservas por día y marca en cuáles hay alguna ocupada.
     * Se usa el solapamiento clásico: una reserva ocupa un bloque si empieza
     * antes de que el bloque termine y termina después de que empiece.
     */
    fun indiceReservasPorDia(
        reservas: List<ReservacionEntity>,
        dias: List<LocalDate>
    ): Map<LocalDate, List<ReservacionEntity>> {
        val porDia = dias.associateWith { mutableListOf<ReservacionEntity>() }
        for (reserva in reservas) {
            porDia[reserva.fecha]?.add(reserva)
        }
        return porDia
    }

    fun reservaEnBloque(
        reservasDelDia: List<ReservacionEntity>,
        indice: Int
    ): ReservacionEntity? {
        val inicioBloque = inicioDeBloque(indice)
        val finBloque = inicioBloque.plusHours(1)
        return reservasDelDia.firstOrNull { reserva ->
            reserva.horaInicio < finBloque && reserva.horaFin > inicioBloque
        }
    }
}

/** "viernes 12 de septiembre" */
fun LocalDate.etiquetaLarga(): String = format(
    DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", EsPanol)
)

/** "12 sep" */
fun LocalDate.etiquetaCorta(): String = format(
    DateTimeFormatter.ofPattern("d MMM", EsPanol)
)

/** "septiembre de 2026" */
fun LocalDate.etiquetaMesAnio(): String = format(
    DateTimeFormatter.ofPattern("LLLL 'de' yyyy", EsPanol)
)

fun LocalTime.etiqueta(): String = format(DateTimeFormatter.ofPattern("HH:mm"))

/** "10:00 - 11:00" */
fun ReservacionEntity.rangoDeHoras(): String = "${horaInicio.etiqueta()} - ${horaFin.etiqueta()}"

/** Datos que el calendario pasa a la pantalla de reservar. */
data class ReservaPendiente(
    val aulaId: Long,
    val aulaNombre: String,
    val edificioNombre: String,
    val fecha: LocalDate,
    val horaInicio: String,
    val horaFin: String
)

package com.example.unispot

import com.example.unispot.data.ReservacionEntity
import com.example.unispot.data.ReservacionRepository
import com.example.unispot.ui.HorarioSemana
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * La ocupación del calendario depende por completo de que el solapamiento se
 * detecte bien: si falla, una misma hora aparece como libre y ocupada.
 */
class HorarioSemanaTest {

    private val fecha = LocalDate.of(2026, 9, 14) // lunes

    private fun reserva(inicio: String, fin: String) = ReservacionEntity(
        usuarioId = 1,
        aulaId = 1,
        titulo = "Prueba",
        detalles = null,
        fecha = fecha,
        horaInicio = LocalTime.parse(inicio),
        horaFin = LocalTime.parse(fin)
    )

    @Test
    fun `un bloque sin reservas queda libre`() {
        assertNull(HorarioSemana.reservaEnBloque(emptyList(), 0))
    }

    @Test
    fun `una reserva ocupa el bloque exacto`() {
        val lista = listOf(reserva("08:00", "09:00"))
        // El bloque 1 es 08:00-09:00.
        assertEquals(lista.first(), HorarioSemana.reservaEnBloque(lista, 1))
    }

    @Test
    fun `una reserva de varias horas ocupa todos sus bloques`() {
        val lista = listOf(reserva("08:00", "10:00"))

        assertEquals(lista.first(), HorarioSemana.reservaEnBloque(lista, 1)) // 08:00-09:00
        assertEquals(lista.first(), HorarioSemana.reservaEnBloque(lista, 2)) // 09:00-10:00
        assertNull(HorarioSemana.reservaEnBloque(lista, 3))                 // 10:00-11:00
        assertNull(HorarioSemana.reservaEnBloque(lista, 0))                 // 07:00-08:00
    }

    @Test
    fun `una reserva parcialmente dentro del bloque tambien lo ocupa`() {
        // 08:30-09:30 se cruza con los bloques de 08 y de 09.
        val lista = listOf(reserva("08:30", "09:30"))

        assertEquals(lista.first(), HorarioSemana.reservaEnBloque(lista, 1))
        assertEquals(lista.first(), HorarioSemana.reservaEnBloque(lista, 2))
        assertNull(HorarioSemana.reservaEnBloque(lista, 0))
        assertNull(HorarioSemana.reservaEnBloque(lista, 3))
    }

    @Test
    fun `reservas consecutivas no se pisan entre si`() {
        val lista = listOf(reserva("08:00", "09:00"), reserva("09:00", "10:00"))

        assertEquals(lista[0], HorarioSemana.reservaEnBloque(lista, 1))
        assertEquals(lista[1], HorarioSemana.reservaEnBloque(lista, 2))
    }

    @Test
    fun `el lunes de la semana se calcula desde cualquier dia`() {
        val jueves = LocalDate.of(2026, 9, 17)
        assertEquals(LocalDate.of(2026, 9, 14), HorarioSemana.lunesDe(jueves))
    }

    @Test
    fun `los dias de la semana son de lunes a viernes`() {
        val dias = HorarioSemana.diasDeLunes(fecha)

        assertEquals(5, dias.size)
        assertEquals(fecha, dias.first())
        assertEquals(LocalDate.of(2026, 9, 18), dias.last())
    }

    @Test
    fun `las horas se convierten a minutos desde medianoche`() {
        assertEquals(0, ReservacionRepository.aMinutos("00:00"))
        assertEquals(600, ReservacionRepository.aMinutos("10:00"))
        assertEquals(1259, ReservacionRepository.aMinutos("20:59"))
    }

    @Test
    fun `el horario del edificio va de 7 a 21 con catorce bloques`() {
        assertEquals(7, HorarioSemana.HORA_INICIO)
        assertEquals(21, HorarioSemana.HORA_FIN)
        assertEquals(14, HorarioSemana.NUM_BLOQUES)
    }
}

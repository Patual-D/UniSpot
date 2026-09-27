package com.example.unispot.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class ReservacionRepository(private val dao: ReservacionDao) {

    fun porUsuario(usuarioId: Long): Flow<List<ReservacionEntity>> = dao.porUsuario(usuarioId)

    fun porAulaYRango(
        aulaId: Long,
        desde: LocalDate,
        hasta: LocalDate
    ): Flow<List<ReservacionEntity>> = dao.porAulaYRango(aulaId, desde, hasta)

    suspend fun insertar(reservacion: ReservacionEntity): Long = dao.insertar(reservacion)

    suspend fun eliminar(reservacion: ReservacionEntity) = dao.eliminar(reservacion)

    suspend fun porId(id: Long): ReservacionEntity? = dao.porId(id)

    /**
     * Indica si el intervalo choca con alguna reserva existente de ese aula.
     * Se consulta en el hilo de IO antes de insertar, y además la base tiene
     * índices por aula y fecha para que la comprobación sea barata.
     */
    suspend fun haySolapamiento(
        aulaId: Long,
        fecha: LocalDate,
        horaInicio: String,
        horaFin: String,
        excluirId: Long = -1
    ): Boolean {
        val inicio = aMinutos(horaInicio)
        val fin = aMinutos(horaFin)
        if (inicio >= fin) return true
        return dao.contarSolapamientos(aulaId, fecha, inicio, fin, excluirId) > 0
    }

    companion object {
        fun aMinutos(hora: String): Int {
            val partes = hora.trim().split(":")
            val h = partes.getOrNull(0)?.toIntOrNull() ?: 0
            val m = partes.getOrNull(1)?.toIntOrNull() ?: 0
            return h * 60 + m
        }
    }
}

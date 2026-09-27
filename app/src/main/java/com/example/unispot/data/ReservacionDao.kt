package com.example.unispot.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservacionDao {

    @Insert
    suspend fun insertar(reservacion: ReservacionEntity): Long

    @Delete
    suspend fun eliminar(reservacion: ReservacionEntity)

    @Query("SELECT * FROM reservaciones WHERE id = :id LIMIT 1")
    suspend fun porId(id: Long): ReservacionEntity?

    /** Reservas del usuario en sesión, para la pantalla "Mis reservas". */
    @Query("SELECT * FROM reservaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC, horaInicio ASC")
    fun porUsuario(usuarioId: Long): Flow<List<ReservacionEntity>>

    /**
     * Todas las reservas de un aula dentro de un rango de fechas. Alimenta la
     * rejilla del calendario: como las horas se guardan como minutos, el
     * solapamiento se resuelve comparando enteros.
     */
    @Query(
        """
        SELECT * FROM reservaciones
        WHERE aulaId = :aulaId AND fecha BETWEEN :desde AND :hasta
        ORDER BY fecha ASC, horaInicio ASC
        """
    )
    fun porAulaYRango(aulaId: Long, desde: LocalDate, hasta: LocalDate): Flow<List<ReservacionEntity>>

    /**
     * Cuenta reservas que se cruzan con el intervalo propuesto. La condición es
     * el solapamiento clásico: empieza antes de que termine el otro y termina
     * después de que empiece el otro. `excluirId` permite ignorar la propia
     * reserva al editar.
     */
    @Query(
        """
        SELECT COUNT(*) FROM reservaciones
        WHERE aulaId = :aulaId
          AND fecha = :fecha
          AND horaInicio < :fin
          AND horaFin > :inicio
          AND id != :excluirId
        """
    )
    suspend fun contarSolapamientos(
        aulaId: Long,
        fecha: LocalDate,
        inicio: Int,
        fin: Int,
        excluirId: Long = -1
    ): Int
}

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

    /**
     * Todas las reservas de un aula dentro de un rango de fechas. Alimenta la
     * rejilla del calendario: como las horas se guardan como minutos, el
     * solapamiento se resuelve comparando enteros.
     *
     * Trae también el nombre de quien reservó, que es lo que muestra el modal.
     */
    @Query(
        """
        SELECT r.*, u.nombre AS nombreUsuario
        FROM reservaciones r
        INNER JOIN usuarios u ON u.id = r.usuarioId
        WHERE r.aulaId = :aulaId AND r.fecha BETWEEN :desde AND :hasta
        ORDER BY r.fecha ASC, r.horaInicio ASC
        """
    )
    fun porAulaYRango(
        aulaId: Long,
        desde: LocalDate,
        hasta: LocalDate
    ): Flow<List<ReservacionConUsuario>>

    /** Igual que [porAulaYRango] pero con las reservas de un solo usuario. */
    @Query(
        """
        SELECT r.*, u.nombre AS nombreUsuario
        FROM reservaciones r
        INNER JOIN usuarios u ON u.id = r.usuarioId
        WHERE r.usuarioId = :usuarioId
        ORDER BY r.fecha DESC, r.horaInicio ASC
        """
    )
    fun porUsuarioConNombre(usuarioId: Long): Flow<List<ReservacionConUsuario>>

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

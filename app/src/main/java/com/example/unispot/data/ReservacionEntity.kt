package com.example.unispot.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * Antes la fecha y las horas se guardaban como texto ("30/08/2026", "10:00"),
 * lo que impedía comparar en SQL y por lo tanto detectar solapamientos.
 * Ahora se usan tipos reales: Room los persiste vía [Converters] y el DAO
 * puede filtrar por aula y rango de fechas, además de validar choques.
 */
@Entity(
    tableName = "reservaciones",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AulaEntity::class,
            parentColumns = ["id"],
            childColumns = ["aulaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId"]),
        Index(value = ["aulaId", "fecha"])
    ]
)
data class ReservacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val aulaId: Long,
    val titulo: String,
    val detalles: String?,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val creadoEn: Long = System.currentTimeMillis(),
    // La migración 2→3 asigna CLASES a lo que ya estaba guardado, así que
    // nunca es nulo aunque se omita al construir la entidad en el código.
    // El default se declara en la columna para que el esquema lo deje escrito
    // y la migración no dependa de un detalle implícito.
    @ColumnInfo(defaultValue = "'CLASES'")
    val categoria: CategoriaReserva = CategoriaReserva.POR_DEFECTO
)

/**
 * Reserva junto al nombre de quien la hizo. El modal lo necesita para decir de
 * quién es, y antes de este JOIN solo se sabía si era propia o ajena.
 */
data class ReservacionConUsuario(
    @Embedded val reserva: ReservacionEntity,
    val nombreUsuario: String
)

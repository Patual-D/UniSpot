package com.example.unispot.data

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
    val creadoEn: Long = System.currentTimeMillis()
)

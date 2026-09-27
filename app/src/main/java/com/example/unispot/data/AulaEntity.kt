package com.example.unispot.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "aulas",
    foreignKeys = [
        ForeignKey(
            entity = EdificioEntity::class,
            parentColumns = ["id"],
            childColumns = ["edificioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["edificioId"])]
)
data class AulaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val edificioId: Long,
    val nombre: String,
    val capacidad: Int
)

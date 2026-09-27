package com.example.unispot.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "edificios",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class EdificioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)

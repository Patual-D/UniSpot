package com.example.unispot.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["matricula"], unique = true),
        Index(value = ["correo"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matricula: String,
    val nombre: String,
    val correo: String,
    /** Hash con sal de la contraseña, nunca el texto plano. Ver [PasswordHasher]. */
    val contrasenaHash: String,
    val creadoEn: Long = System.currentTimeMillis()
)

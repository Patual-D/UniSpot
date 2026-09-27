package com.example.unispot.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    suspend fun contarPorCorreo(correo: String): Int

    @Query("SELECT COUNT(*) FROM usuarios WHERE matricula = :matricula")
    suspend fun contarPorMatricula(matricula: String): Int
}

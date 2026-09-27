package com.example.unispot.data

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** Un aula junto con el edificio al que pertenece, para pintar la ruta completa. */
data class AulaConEdificio(
    @Embedded val aula: AulaEntity,
    @Relation(parentColumn = "edificioId", entityColumn = "id")
    val edificio: EdificioEntity
)

@Dao
interface AulaDao {

    @Insert
    suspend fun insertarTodas(aulas: List<AulaEntity>)

    @Query("SELECT * FROM aulas WHERE edificioId = :edificioId ORDER BY nombre ASC")
    fun porEdificio(edificioId: Long): Flow<List<AulaEntity>>

    @Transaction
    @Query("SELECT * FROM aulas ORDER BY id ASC")
    fun todasConEdificio(): Flow<List<AulaConEdificio>>

    @Query("SELECT * FROM aulas WHERE id = :id LIMIT 1")
    suspend fun porId(id: Long): AulaEntity?

    @Query("SELECT COUNT(*) FROM aulas")
    suspend fun contar(): Int
}

@Dao
interface EdificioDao {

    @Insert
    suspend fun insertarTodos(edificios: List<EdificioEntity>): List<Long>

    @Query("SELECT * FROM edificios ORDER BY id ASC")
    fun todos(): Flow<List<EdificioEntity>>

    @Query("SELECT COUNT(*) FROM edificios")
    suspend fun contar(): Int
}

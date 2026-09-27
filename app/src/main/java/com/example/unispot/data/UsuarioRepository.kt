package com.example.unispot.data

import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun crear(usuario: UsuarioEntity): Long = dao.insertar(usuario)

    suspend fun buscarPorCorreo(correo: String): UsuarioEntity? =
        dao.buscarPorCorreo(correo.trim().lowercase())

    suspend fun buscarPorId(id: Long): UsuarioEntity? = dao.buscarPorId(id)

    suspend fun existeCorreo(correo: String): Boolean = dao.contarPorCorreo(correo) > 0

    suspend fun existeMatricula(matricula: String): Boolean = dao.contarPorMatricula(matricula) > 0
}

class CatalogoRepository(private val edificioDao: EdificioDao, private val aulaDao: AulaDao) {

    val edificios: Flow<List<EdificioEntity>> = edificioDao.todos()

    fun aulasDe(edificioId: Long): Flow<List<AulaEntity>> = aulaDao.porEdificio(edificioId)

    suspend fun aulaPorId(id: Long): AulaEntity? = aulaDao.porId(id)
}

package com.example.unispot.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Catálogo de edificios y aulas. Se puebla solo la primera vez: así las
 * pantallas navegan sobre datos guardados en la base y no sobre listas escritas
 * a mano en el código.
 */
class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val base = UniSpotDatabase.obtenerBaseDeDatos(application)
    private val edificioDao = base.edificioDao()
    private val aulaDao = base.aulaDao()

    val edificios: StateFlow<List<EdificioEntity>> = edificioDao.todos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Indexado por id de aula, para resolver nombres al mostrar reservas. */
    val aulasPorId: StateFlow<Map<Long, AulaConEdificio>> = aulaDao.todasConEdificio()
        .map { lista -> lista.associateBy { it.aula.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            CatalogoInicial.poblarSiEstaVacio(edificioDao, aulaDao)
        }
    }

    fun aulasDe(edificioId: Long) = aulaDao.porEdificio(edificioId)
}

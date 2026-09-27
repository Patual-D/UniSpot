package com.example.unispot.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReservacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReservacionRepository

    /** Lo fija la app cuando hay sesión; mientras tanto no se consulta nada. */
    private val _usuarioId = MutableStateFlow(-1L)

    private val _errorReserva = MutableStateFlow<String?>(null)
    val errorReserva: StateFlow<String?> = _errorReserva.asStateFlow()

    /** Id de la última reserva guardada; permite cerrar el formulario solo si el alta funcionó. */
    private val _ultimaReservaGuardadaId = MutableStateFlow<Long?>(null)
    val ultimaReservaGuardadaId: StateFlow<Long?> = _ultimaReservaGuardadaId.asStateFlow()

    /** Reacciona a la sesión: si el usuario cambia, la lista se recalcula sola. */
    val misReservaciones: Flow<List<ReservacionEntity>> = _usuarioId.flatMapLatest { id ->
        if (id == -1L) flowOf(emptyList()) else repository.porUsuario(id)
    }

    init {
        val dao = UniSpotDatabase.obtenerBaseDeDatos(application).reservacionDao()
        repository = ReservacionRepository(dao)
    }

    fun establecerUsuario(usuarioId: Long) {
        if (_usuarioId.value != usuarioId) _usuarioId.value = usuarioId
    }

    /** Reservas del aula en el rango indicado, para pintar la rejilla del calendario. */
    fun reservasDelAula(
        aulaId: Long,
        desde: LocalDate,
        hasta: LocalDate
    ): Flow<List<ReservacionEntity>> = repository.porAulaYRango(aulaId, desde, hasta)

    fun limpiarErrorReserva() {
        _errorReserva.value = null
    }

    fun limpiarReservaGuardada() {
        _ultimaReservaGuardadaId.value = null
    }

    /**
     * Valida el intervalo contra las reservas del mismo aula antes de insertar.
     * La comprobación y el alta se hacen fuera del hilo principal porque son
     * consultas a la base de datos.
     */
    fun reservar(
        usuarioId: Long,
        aulaId: Long,
        titulo: String,
        detalles: String,
        fecha: LocalDate,
        horaInicio: String,
        horaFin: String
    ) {
        if (titulo.isBlank()) {
            _errorReserva.value = "Ponle un título a la reservación"
            return
        }

        if (aulaId <= 0) {
            _errorReserva.value = "No se pudo identificar el aula. Vuelve a elegir una."
            return
        }

        val inicio = ReservacionRepository.aMinutos(horaInicio)
        val fin = ReservacionRepository.aMinutos(horaFin)

        if (inicio >= fin) {
            _errorReserva.value = "La hora de fin debe ser posterior a la de inicio"
            return
        }

        _errorReserva.value = null

        viewModelScope.launch(Dispatchers.IO) {
            if (repository.haySolapamiento(aulaId, fecha, horaInicio, horaFin)) {
                _errorReserva.value = "Ese horario ya está reservado"
                return@launch
            }

            repository.insertar(
                ReservacionEntity(
                    usuarioId = usuarioId,
                    aulaId = aulaId,
                    titulo = titulo.trim(),
                    detalles = detalles.trim().ifBlank { null },
                    fecha = fecha,
                    horaInicio = aLocalTime(horaInicio),
                    horaFin = aLocalTime(horaFin)
                )
            ).let { _ultimaReservaGuardadaId.value = it }
        }
    }

    fun eliminar(reservacion: ReservacionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.eliminar(reservacion)
        }
    }

    private fun aLocalTime(hora: String): LocalTime {
        val partes = hora.trim().split(":")
        return LocalTime.of(
            partes.getOrNull(0)?.toIntOrNull() ?: 0,
            partes.getOrNull(1)?.toIntOrNull() ?: 0
        )
    }
}

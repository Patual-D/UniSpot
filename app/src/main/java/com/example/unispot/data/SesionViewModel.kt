package com.example.unispot.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Dueño de la sesión: sabe qué usuario está autenticado. Antes el login
 * aceptaba cualquier valor y no guardaba nada, así que el perfil no tenía de
 * dónde sacar los datos.
 */
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val sesionStore = SesionStore(application)
    private val usuarioRepository: UsuarioRepository

    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    /** Verdadero mientras se consulta la cuenta guardada, para no mostrar login sin querer. */
    private val _restaurando = MutableStateFlow(true)
    val restaurando: StateFlow<Boolean> = _restaurando.asStateFlow()

    init {
        val dao = UniSpotDatabase.obtenerBaseDeDatos(application).usuarioDao()
        usuarioRepository = UsuarioRepository(dao)
        restaurarSesion()
    }

    /** Recupera la cuenta de la sesión anterior, si sigue existiendo. */
    private fun restaurarSesion() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = sesionStore.usuarioId
            val usuario = if (id == -1L) null else usuarioRepository.buscarPorId(id)
            if (usuario == null) sesionStore.limpiar() else sesionStore.usuarioId = usuario.id
            _usuarioActual.value = usuario
            _restaurando.value = false
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        if (_procesando.value) return

        viewModelScope.launch {
            _procesando.value = true
            _mensajeError.value = null

            val error = ValidacionUsuario.errorCorreo(correo)
                ?: ValidacionUsuario.errorContrasena(contrasena)
            if (error != null) {
                _mensajeError.value = error
                _procesando.value = false
                return@launch
            }

            val resultado = withContext(Dispatchers.IO) {
                val usuario = usuarioRepository.buscarPorCorreo(correo)
                when {
                    usuario == null -> "No hay ninguna cuenta con ese correo"
                    !PasswordHasher.verificar(contrasena, usuario.contrasenaHash) ->
                        "La contraseña es incorrecta"
                    else -> {
                        sesionStore.usuarioId = usuario.id
                        usuario
                    }
                }
            }

            if (resultado is String) {
                _mensajeError.value = resultado
            } else {
                _usuarioActual.value = resultado as UsuarioEntity
            }
            _procesando.value = false
        }
    }

    fun registrarCuenta(
        matricula: String,
        nombre: String,
        correo: String,
        contrasena: String,
        confirmacion: String
    ) {
        if (_procesando.value) return

        viewModelScope.launch {
            _procesando.value = true
            _mensajeError.value = null

            // Validación de formato antes de tocar la base.
            val errorFormato = ValidacionUsuario.errorMatricula(matricula)
                ?: ValidacionUsuario.errorNombre(nombre)
                ?: ValidacionUsuario.errorCorreo(correo)
                ?: ValidacionUsuario.errorContrasena(contrasena)
                ?: ValidacionUsuario.errorConfirmacion(contrasena, confirmacion)

            if (errorFormato != null) {
                _mensajeError.value = errorFormato
                _procesando.value = false
                return@launch
            }

            val correoNormalizado = ValidacionUsuario.correoNormalizado(correo)
            val matriculaNormalizada = matricula.trim()

            val resultado: Any = withContext(Dispatchers.IO) {
                when {
                    usuarioRepository.existeMatricula(matriculaNormalizada) ->
                        "Esa matrícula ya está registrada"
                    usuarioRepository.existeCorreo(correoNormalizado) ->
                        "Ese correo ya está registrado"
                    else -> {
                        // El hash es costoso a propósito, así que va fuera del hilo principal.
                        val nuevo = UsuarioEntity(
                            matricula = matriculaNormalizada,
                            nombre = nombre.trim(),
                            correo = correoNormalizado,
                            contrasenaHash = PasswordHasher.hashear(contrasena)
                        )
                        usuarioRepository.crear(nuevo)
                    }
                }
            }

            if (resultado is String) {
                _mensajeError.value = resultado
            } else {
                val id = resultado as Long
                sesionStore.usuarioId = id
                _usuarioActual.value = usuarioRepository.buscarPorId(id)
            }
            _procesando.value = false
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch(Dispatchers.IO) {
            sesionStore.limpiar()
            _usuarioActual.value = null
            _mensajeError.value = null
        }
    }
}

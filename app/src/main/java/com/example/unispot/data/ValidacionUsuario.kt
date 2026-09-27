package com.example.unispot.data

import android.util.Patterns

/** Reglas de validación de la cuenta, separadas de la UI para poder probarlas. */
object ValidacionUsuario {

    const val LONGITUD_MINIMA_CONTRASENA = 6

    fun errorMatricula(matricula: String): String? = when {
        matricula.isBlank() -> "Escribe tu número de matrícula"
        matricula.trim().length < 4 -> "La matrícula debe tener al menos 4 caracteres"
        else -> null
    }

    fun errorNombre(nombre: String): String? = when {
        nombre.isBlank() -> "Escribe tu nombre"
        nombre.trim().length < 3 -> "Escribe tu nombre completo"
        else -> null
    }

    fun errorCorreo(correo: String): String? = when {
        correo.isBlank() -> "Escribe tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() ->
            "Ese correo no tiene un formato válido"
        else -> null
    }

    fun errorContrasena(contrasena: String): String? = when {
        contrasena.isBlank() -> "Escribe una contraseña"
        contrasena.length < LONGITUD_MINIMA_CONTRASENA ->
            "La contraseña debe tener al menos $LONGITUD_MINIMA_CONTRASENA caracteres"
        else -> null
    }

    fun errorConfirmacion(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña"
        confirmacion != contrasena -> "Las contraseñas no coinciden"
        else -> null
    }

    fun correoNormalizado(correo: String): String = correo.trim().lowercase()
}

package com.example.unispot.data

import android.content.Context

/**
 * Guarda el identificador del usuario en sesión. Sin esto, cerrar y reabrir la
 * app devolvería al usuario a la pantalla de login aunque su cuenta siga
 * existiendo en la base local.
 */
class SesionStore(context: Context) {

    private val preferencias =
        context.applicationContext.getSharedPreferences("unispot_sesion", Context.MODE_PRIVATE)

    var usuarioId: Long
        get() = preferencias.getLong(CLAVE_USUARIO, -1L)
        set(valor) {
            preferencias.edit().apply {
                if (valor == -1L) remove(CLAVE_USUARIO) else putLong(CLAVE_USUARIO, valor)
            }.apply()
        }

    fun limpiar() {
        preferencias.edit().clear().apply()
    }

    private companion object {
        const val CLAVE_USUARIO = "usuario_id"
    }
}

package com.example.unispot.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria por usuario.
 * La base de datos local es accesible para cualquiera con el dispositivo
 * desbloqueado, así que nunca debe guardarse la contraseña en texto plano.
 */
object PasswordHasher {

    private const val ITERACIONES = 120_000
    private const val LONGITUD_CLAVE_BITS = 256
    private const val LONGITUD_SAL = 16

    /** Formato almacenado: `sal.hash`, ambos en Base64. */
    fun hashear(contrasena: String): String {
        val sal = ByteArray(LONGITUD_SAL).also { SecureRandom().nextBytes(it) }
        val hash = derivar(contrasena, sal, LONGITUD_CLAVE_BITS)
        return "${Base64.encodeToString(sal, Base64.NO_WRAP)}." +
            Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verificar(contrasena: String, almacenado: String): Boolean {
        val partes = almacenado.split(".")
        if (partes.size != 2) return false

        return try {
            val sal = Base64.decode(partes[0], Base64.NO_WRAP)
            val esperado = Base64.decode(partes[1], Base64.NO_WRAP)
            val calculado = derivar(contrasena, sal, esperado.size * 8)
            // Comparación en tiempo constante para no filtrar información por temporización.
            MessageDigest.isEqual(esperado, calculado)
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun derivar(contrasena: String, sal: ByteArray, longitudBits: Int): ByteArray {
        val spec = PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, longitudBits)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    }
}

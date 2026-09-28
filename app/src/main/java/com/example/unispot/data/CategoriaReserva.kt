package com.example.unispot.data

/**
 * Tipo de reserva. El color de la celda en el calendario depende de la
 * categoría, y por eso existe: antes todas las reservadas se veían igual.
 *
 * Se guarda la [clave] y no el [nombre] a propósito. Si mañana se cambia
 * "Punto de Encuentro" por "Punto de encuentro", los datos siguen siendo válidos
 * porque en la base hay una clave, no una etiqueta.
 */
enum class CategoriaReserva(
    val clave: String,
    val nombre: String
) {
    CLASES("CLASES", "Clases"),
    TALLERES("TALLERES", "Talleres"),
    PUNTO_DE_ENCUENTRO("PUNTO_ENCUENTRO", "Punto de Encuentro"),
    JUNTAS_Y_CONFERENCIAS("JUNTAS_CONFERENCIAS", "Juntas/Conferencias"),
    ZONA_ESTUDIANTIL("ZONA_ESTUDIANTIL", "Zona estudiantil");

    companion object {
        /** La que se marca por defecto al crear una reserva. */
        val POR_DEFECTO = CLASES

        /**
         * Convierte el texto guardado en la base. Si la clave no coincide
         * devuelve [POR_DEFECTO] en vez de fallar: una categoría desconocida no
         * debe impedir ver la reserva.
         */
        fun desdeClave(clave: String?): CategoriaReserva =
            entries.firstOrNull { it.clave == clave } ?: POR_DEFECTO
    }
}

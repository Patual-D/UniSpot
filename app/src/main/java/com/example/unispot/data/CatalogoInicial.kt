package com.example.unispot.data

/**
 * Catálogo inicial de edificios y aulas. Se inserta una sola vez, la primera
 * vez que se abre la base, para que la navegación entre edificios y aulas
 * funcione con datos reales en lugar de listas escritas a mano en la UI.
 */
object CatalogoInicial {

    data class AulaSemilla(val nombre: String, val capacidad: Int)

    val EDIFICIOS = listOf(
        "Biblioteca" to listOf(
            AulaSemilla("Sala de Estudio 1", 30),
            AulaSemilla("Sala de Estudio 2", 30),
            AulaSemilla("Sala de Estudio 3", 20)
        ),
        "Edificio 1" to listOf(
            AulaSemilla("Aula 101", 40),
            AulaSemilla("Aula 102", 40),
            AulaSemilla("Aula 103", 35),
            AulaSemilla("Aula 104", 35),
            AulaSemilla("Aula 105", 30),
            AulaSemilla("Aula 106", 30),
            AulaSemilla("Aula 201", 45),
            AulaSemilla("Aula 202", 45),
            AulaSemilla("Aula 203", 25),
            AulaSemilla("Aula 204", 25)
        ),
        "Edificio 2" to listOf(
            AulaSemilla("Aula 201", 40),
            AulaSemilla("Aula 202", 40),
            AulaSemilla("Laboratorio 1", 24),
            AulaSemilla("Laboratorio 2", 24)
        ),
        "Oficinas" to listOf(
            AulaSemilla("Oficina 1", 8),
            AulaSemilla("Oficina 2", 8),
            AulaSemilla("Sala de juntas", 12)
        ),
        "Auditorio" to listOf(
            AulaSemilla("Auditorio A", 200),
            AulaSemilla("Auditorio B", 120)
        )
    )

    suspend fun poblarSiEstaVacio(edificioDao: EdificioDao, aulaDao: AulaDao) {
        if (edificioDao.contar() > 0) return

        for ((nombreEdificio, aulas) in EDIFICIOS) {
            val ids = edificioDao.insertarTodos(listOf(EdificioEntity(nombre = nombreEdificio)))
            aulaDao.insertarTodas(
                aulas.map { aula ->
                    AulaEntity(
                        edificioId = ids.first(),
                        nombre = aula.nombre,
                        capacidad = aula.capacidad
                    )
                }
            )
        }
    }
}

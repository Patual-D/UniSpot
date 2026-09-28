package com.example.unispot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.data.UsuarioEntity
import com.example.unispot.ui.ControladorNavegacion
import com.example.unispot.ui.Pantalla
import com.example.unispot.ui.ReservaPendiente
import com.example.unispot.ui.components.BotonLista
import com.example.unispot.ui.components.TituloPantalla
import com.example.unispot.ui.theme.UniSpotTheme
import java.time.LocalDate

private val ANCHO_PANEL_IZQUIERDO = 320.dp

/**
 * Distribución de dos paneles para tablet.
 *
 * En una pantalla ancha no tiene sentido que elegir un aula te saque de la
 * lista: aquí la lista de edificios y aulas se queda siempre visible a la
 * izquierda y el horario del aula elegida aparece a la derecha, sin navegar.
 * En móvil se mantiene el flujo de una sola columna.
 */
@Composable
fun PanelDualScreen(
    usuario: UsuarioEntity,
    catalogo: CatalogoViewModel,
    reservaciones: ReservacionViewModel,
    controlador: ControladorNavegacion,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        PanelExplorador(
            catalogo = catalogo,
            controlador = controlador,
            modifier = Modifier
                .width(ANCHO_PANEL_IZQUIERDO)
                .fillMaxHeight()
        )

        VerticalDivider(color = UniSpotTheme.colors.bordeCelda)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            when (controlador.pantalla) {
                Pantalla.RESERVAS -> MisReservasScreen(
                    viewModel = reservaciones,
                    catalogo = catalogo
                )

                Pantalla.PERFIL -> PerfilScreen(
                    usuario = usuario,
                    onCerrarSesion = onCerrarSesion
                )

                else -> PanelDetalle(
                    usuario = usuario,
                    reservaciones = reservaciones,
                    controlador = controlador
                )
            }
        }
    }
}

/** Panel izquierdo: edificios y, al elegir uno, sus aulas. */
@Composable
private fun PanelExplorador(
    catalogo: CatalogoViewModel,
    controlador: ControladorNavegacion,
    modifier: Modifier = Modifier
) {
    val edificios by catalogo.edificios.collectAsStateWithLifecycle()
    val aulasPorId by catalogo.aulasPorId.collectAsStateWithLifecycle()

    val edificio = controlador.edificio
    val aulas = if (edificio == null) {
        emptyList()
    } else {
        aulasPorId.values.filter { it.edificio.id == edificio.id }.map { it.aula }
    }
    val colores = UniSpotTheme.colors

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.heightIn(min = 16.dp))
        TituloPantalla("Edificios")
        Spacer(modifier = Modifier.heightIn(min = 12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (edificios.isEmpty()) {
                item(key = "cargando") {
                    Text(
                        "Cargando edificios...",
                        fontSize = 13.sp,
                        color = colores.textoSecundario,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            items(edificios, key = { "edificio-${it.id}" }) { item ->
                BotonLista(
                    texto = item.nombre,
                    onClick = { controlador.elegirEdificio(item) },
                    seleccionado = edificio?.id == item.id
                )
            }

            // Las aulas del edificio elegido cuelgan de la misma lista para que
            // todo el panel se desplace junto, sin dos desplazamientos anidados.
            if (edificio != null) {
                item(key = "encabezado-${edificio.id}") {
                    Column {
                        Spacer(modifier = Modifier.heightIn(min = 18.dp))
                        Text(
                            "Aulas de ${edificio.nombre}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniSpotTheme.colors.textoSecundario,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .semantics { heading() }
                        )
                        Spacer(modifier = Modifier.heightIn(min = 6.dp))
                    }
                }

                items(aulas, key = { "aula-${it.id}" }) { aula ->
                    BotonLista(
                        texto = aula.nombre,
                        subtitulo = "${aula.capacidad} lugares",
                        onClick = { controlador.elegirAula(aula) },
                        seleccionado = controlador.aula?.id == aula.id
                    )
                }
            }
        }
    }
}

/** Panel derecho: el horario del aula elegida, o el formulario de reserva. */
@Composable
private fun PanelDetalle(
    usuario: UsuarioEntity,
    reservaciones: ReservacionViewModel,
    controlador: ControladorNavegacion
) {
    val edificio = controlador.edificio
    val aula = controlador.aula

    when {
        controlador.pantalla == Pantalla.RESERVAR && aula != null -> ReservarScreen(
            pendiente = controlador.reservaPendiente ?: ReservaPendiente(
                aulaId = aula.id,
                aulaNombre = aula.nombre,
                edificioNombre = edificio?.nombre.orEmpty(),
                fecha = LocalDate.now(),
                horaInicio = "10:00",
                horaFin = "11:00"
            ),
            usuarioId = usuario.id,
            viewModel = reservaciones,
            onGuardada = {
                controlador.limpiarPendiente()
                controlador.irA(Pantalla.HORARIO)
            },
            onCancelar = {
                controlador.limpiarPendiente()
                controlador.irA(Pantalla.HORARIO)
            }
        )

        aula != null -> HorarioScreen(
            aulaId = aula.id,
            aulaNombre = aula.nombre,
            edificioNombre = edificio?.nombre.orEmpty(),
            usuarioIdActual = usuario.id,
            viewModel = reservaciones,
            onReservar = { controlador.prepararReserva(it) }
        )

        else -> PanelVacio()
    }
}

@Composable
private fun PanelVacio() {
    val colores = UniSpotTheme.colors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = colores.verdeClaro
            )

            Spacer(modifier = Modifier.heightIn(min = 16.dp))

            Text(
                "Elige un aula",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.heightIn(min = 6.dp))

            Text(
                "Selecciona un edificio y luego un aula en el panel de la izquierda " +
                    "para ver su horario y reservar.",
                color = colores.textoSecundario,
                textAlign = TextAlign.Center
            )
        }
    }
}

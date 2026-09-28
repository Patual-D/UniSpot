package com.example.unispot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.ReservacionEntity
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.ui.EsPanol
import com.example.unispot.ui.HorarioSemana
import com.example.unispot.ui.ReservaPendiente
import com.example.unispot.ui.components.AreaTactilMinima
import com.example.unispot.ui.components.ConfirmacionBorrado
import com.example.unispot.ui.etiquetaCorta
import com.example.unispot.ui.etiquetaLarga
import com.example.unispot.ui.etiquetaMesAnio
import com.example.unispot.ui.rangoDeHoras
import com.example.unispot.ui.theme.UniSpotTheme
import java.time.LocalDate
import java.time.format.TextStyle

private val ANCHO_EJE_HORAS = 56.dp

/**
 * Rejilla semanal real: 07:00 a 21:00 en bloques de una hora, de lunes a
 * viernes. Los datos vienen de la base y las celdas son pulsables.
 */
@Composable
fun HorarioScreen(
    aulaId: Long,
    aulaNombre: String,
    edificioNombre: String,
    usuarioIdActual: Long,
    viewModel: ReservacionViewModel,
    onReservar: (ReservaPendiente) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoy = remember { LocalDate.now() }
    var lunes by remember { mutableStateOf(HorarioSemana.lunesDe(hoy)) }
    var reservaAMostrar by remember { mutableStateOf<ReservacionEntity?>(null) }
    var confirmandoBorrado by remember { mutableStateOf<ReservacionEntity?>(null) }

    val dias = remember(lunes) { HorarioSemana.diasDeLunes(lunes) }

    val reservas by remember(aulaId, lunes) {
        viewModel.reservasDelAula(aulaId, dias.first(), dias.last())
    }.collectAsStateWithLifecycle(emptyList())

    val reservasPorDia = remember(reservas, dias) {
        HorarioSemana.indiceReservasPorDia(reservas, dias)
    }

    // Una reserva de varias horas ocupa varias celdas, así que los "libres" se
    // cuentan por celda ocupada y no por número de reservas.
    val ocupacion = remember(reservasPorDia, dias) {
        HorarioSemana.ocupacionDeSemana(reservasPorDia, dias)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                edificioNombre,
                fontSize = 14.sp,
                color = UniSpotTheme.colors.textoSecundario
            )

            Text(
                aulaNombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.heightIn(min = 8.dp))

            // Navegación de semana. Sin esto el calendario solo mostraba una
            // semana fija que nunca cambiaba.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { lunes = lunes.minusWeeks(1) },
                    modifier = Modifier.size(AreaTactilMinima)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Ver semana anterior",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${dias.first().etiquetaCorta()} - ${dias.last().etiquetaCorta()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        dias.first().etiquetaMesAnio(),
                        fontSize = 12.sp,
                        color = UniSpotTheme.colors.textoSecundario
                    )
                }

                IconButton(
                    onClick = { lunes = lunes.plusWeeks(1) },
                    modifier = Modifier.size(AreaTactilMinima)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ver semana siguiente",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Resumen: 70 celdas seguidas no son navegables con TalkBack,
                // así que primero se da la idea y luego el detalle.
                val plural = if (reservas.size == 1) "reserva" else "reservas"
                Text(
                    "${ocupacion.libres} libres · ${reservas.size} $plural",
                    fontSize = 13.sp,
                    color = UniSpotTheme.colors.textoSecundario,
                    modifier = Modifier.semantics {
                        contentDescription = "Esta semana hay ${ocupacion.libres} horarios " +
                            "libres y ${ocupacion.ocupados} reservados"
                    }
                )
                TextButton(
                    onClick = { lunes = HorarioSemana.lunesDe(hoy) },
                    modifier = Modifier.heightIn(min = AreaTactilMinima)
                ) {
                    Text("Hoy", color = UniSpotTheme.colors.verdeOscuro, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.heightIn(min = 4.dp))
        }

        EncabezadosDias(dias, hoy)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .width(ANCHO_EJE_HORAS)
                        .padding(start = 12.dp)
                ) {
                    repeat(HorarioSemana.NUM_BLOQUES) { indice ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                // heightIn en vez de height fijo: con letra
                                // grande el texto se cortaba.
                                .heightIn(min = 48.dp)
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                HorarioSemana.etiquetaHora(indice),
                                fontSize = 12.sp,
                                color = UniSpotTheme.colors.textoSecundario
                            )
                        }
                    }
                }

                dias.forEach { fecha ->
                    val reservasDelDia = reservasPorDia[fecha].orEmpty()

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                    ) {
                        repeat(HorarioSemana.NUM_BLOQUES) { indice ->
                            val reserva = HorarioSemana.reservaEnBloque(reservasDelDia, indice)
                            CeldaHorario(
                                reserva = reserva,
                                fecha = fecha,
                                indiceBloque = indice,
                                onCeldaLibreClick = {
                                    onReservar(
                                        ReservaPendiente(
                                            aulaId = aulaId,
                                            aulaNombre = aulaNombre,
                                            edificioNombre = edificioNombre,
                                            fecha = fecha,
                                            horaInicio = HorarioSemana.etiquetaHora(indice),
                                            horaFin = HorarioSemana.inicioDeBloque(indice)
                                                .plusHours(1)
                                                .toString()
                                                .substring(0, 5)
                                        )
                                    )
                                },
                                onCeldaReservadaClick = { reservaAMostrar = reserva }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.heightIn(min = 16.dp))
        }

        LeyendaHorario()
    }

    // Detalle de una reserva ya existente.
    val reserva = reservaAMostrar
    if (reserva != null) {
        val esMia = reserva.usuarioId == usuarioIdActual
        AlertDialog(
            onDismissRequest = { reservaAMostrar = null },
            title = { Text(reserva.titulo, modifier = Modifier.semantics { heading() }) },
            text = {
                Column {
                    Text(reserva.fecha.etiquetaLarga())
                    Text(reserva.rangoDeHoras())
                    if (!reserva.detalles.isNullOrBlank()) {
                        Spacer(modifier = Modifier.heightIn(min = 8.dp))
                        Text(reserva.detalles)
                    }
                    Spacer(modifier = Modifier.heightIn(min = 8.dp))
                    Text(
                        if (esMia) "Reservación tuya" else "Reservado por otro usuario",
                        fontSize = 12.sp,
                        color = UniSpotTheme.colors.textoSecundario
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { reservaAMostrar = null }) { Text("Cerrar") }
            },
            dismissButton = if (esMia) {
                {
                    Button(
                        onClick = {
                            confirmandoBorrado = reserva
                            reservaAMostrar = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            } else {
                null
            }
        )
    }

    confirmandoBorrado?.let { aBorrar ->
        ConfirmacionBorrado(
            mensaje = "Vas a eliminar la reservación \"${aBorrar.titulo}\" del " +
                "${aBorrar.fecha.etiquetaLarga()}. Esta acción no se puede deshacer.",
            onConfirmar = {
                viewModel.eliminar(aBorrar)
                confirmandoBorrado = null
            },
            onCancelar = { confirmandoBorrado = null }
        )
    }
}

@Composable
private fun EncabezadosDias(dias: List<LocalDate>, hoy: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, start = 12.dp, end = 4.dp)
    ) {
        Spacer(modifier = Modifier.width(ANCHO_EJE_HORAS))

        dias.forEachIndexed { indice, fecha ->
            val esHoy = fecha == hoy
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .semantics {
                        heading()
                        contentDescription = "${HorarioSemana.ENCABEZADOS[indice]} " +
                            "${fecha.dayOfMonth} de " +
                            fecha.month.getDisplayName(TextStyle.FULL, EsPanol) +
                            if (esHoy) ", hoy" else ""
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .background(
                            if (esHoy) UniSpotTheme.colors.columnaHoy else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        HorarioSemana.ENCABEZADOS[indice],
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esHoy) {
                            UniSpotTheme.colors.verdeOscuro
                        } else {
                            UniSpotTheme.colors.textoSecundario
                        }
                    )
                    Text(
                        "${fecha.dayOfMonth}",
                        fontSize = 11.sp,
                        color = if (esHoy) {
                            UniSpotTheme.colors.verdeOscuro
                        } else {
                            UniSpotTheme.colors.textoSecundario
                        }
                    )
                }
            }
        }
    }
}

/**
 * Una celda del horario. Libre y reservada comparten forma y tamaño: lo único
 * que cambia es el color de fondo, para que se distinga de un vistazo.
 */
@Composable
private fun CeldaHorario(
    reserva: ReservacionEntity?,
    fecha: LocalDate,
    indiceBloque: Int,
    onCeldaLibreClick: () -> Unit,
    onCeldaReservadaClick: (ReservacionEntity) -> Unit
) {
    val colores = UniSpotTheme.colors
    val horaInicio = HorarioSemana.inicioDeBloque(indiceBloque)

    // El título aparece solo en el bloque donde arranca la reserva, para no
    // repetirlo cinco veces si ocupa varias horas seguidas.
    val muestraTitulo = reserva != null && reserva.horaInicio >= horaInicio

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(vertical = 1.dp)
            .background(
                if (reserva == null) colores.celdaLibre else colores.celdaReservada,
                RoundedCornerShape(6.dp)
            )
            .border(1.dp, colores.bordeCelda, RoundedCornerShape(6.dp))
            .clickable {
                if (reserva == null) onCeldaLibreClick() else onCeldaReservadaClick(reserva)
            }
            .semantics {
                stateDescription = if (reserva == null) "Libre" else "Reservado"
                contentDescription = if (reserva == null) {
                    "Libre, ${fecha.etiquetaLarga()}, " +
                        HorarioSemana.etiquetaHora(indiceBloque)
                } else {
                    "Reservado, ${reserva.titulo}, ${reserva.rangoDeHoras()}, " +
                        fecha.etiquetaLarga()
                }
            }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (muestraTitulo) {
            Text(
                reserva!!.titulo,
                color = colores.contenidoSobreVerdeOscuro,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LeyendaHorario() {
    val colores = UniSpotTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemLeyenda(color = colores.celdaLibre, texto = "Libre")
        ItemLeyenda(color = colores.celdaReservada, texto = "Reservado")
    }
}

@Composable
private fun ItemLeyenda(color: Color, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        // La leyenda es informativa, no un control: no debe anunciarse.
        modifier = Modifier.clearAndSetSemantics {}
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, fontSize = 12.sp, color = UniSpotTheme.colors.textoSecundario)
    }
}

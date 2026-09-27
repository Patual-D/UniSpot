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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.ReservacionEntity
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.ui.HorarioSemana
import com.example.unispot.ui.ReservaPendiente
import com.example.unispot.ui.etiquetaCorta
import com.example.unispot.ui.etiquetaLarga
import com.example.unispot.ui.etiquetaMesAnio
import com.example.unispot.ui.rangoDeHoras
import com.example.unispot.ui.theme.CeldaBorde
import com.example.unispot.ui.theme.CeldaLibre
import com.example.unispot.ui.theme.CeldaReservada
import com.example.unispot.ui.theme.ColumnaHoy
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.VerdeOscuro
import java.time.LocalDate

private val ALTO_CELDA = 46.dp
private val ANCHO_EJE_HORAS = 52.dp

/**
 * Rejilla semanal real: 07:00 a 21:00 en bloques de una hora, de lunes a
 * viernes. Los datos vienen de la base y las celdas son pulsables.
 *
 * Antes esta pantalla no consultaba nada: la ocupación salía de
 * `(fila + columna) % 3 == 0` y los títulos eran texto fijo.
 */
@Composable
fun HorarioScreen(
    aulaId: Long,
    aulaNombre: String,
    edificioNombre: String,
    usuarioIdActual: Long,
    viewModel: ReservacionViewModel,
    onReservar: (ReservaPendiente) -> Unit
) {
    val hoy = remember { LocalDate.now() }
    var lunes by remember { mutableStateOf(HorarioSemana.lunesDe(hoy)) }
    var reservaAMostrar by remember { mutableStateOf<ReservacionEntity?>(null) }

    val dias = remember(lunes) { HorarioSemana.diasDeLunes(lunes) }

    // El flow se recrea al cambiar de semana, así que la rejilla se recarga sola.
    val reservas by remember(aulaId, lunes) {
        viewModel.reservasDelAula(aulaId, dias.first(), dias.last())
    }.collectAsStateWithLifecycle(emptyList())

    val reservasPorDia = remember(reservas, dias) {
        HorarioSemana.indiceReservasPorDia(reservas, dias)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                edificioNombre,
                fontSize = 14.sp,
                color = VerdeOscuro.copy(alpha = 0.8f)
            )

            Text(
                aulaNombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Navegación de semana. Sin esto el calendario solo mostraba una
            // semana fija que nunca cambiaba.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { lunes = lunes.minusWeeks(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Semana anterior",
                        tint = VerdeOscuro
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${dias.first().etiquetaCorta()} - ${dias.last().etiquetaCorta()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        dias.first().etiquetaMesAnio(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = { lunes = lunes.plusWeeks(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Semana siguiente",
                        tint = VerdeOscuro
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${reservas.size} ${if (reservas.size == 1) "reserva" else "reservas"} esta semana",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                TextButton(onClick = { lunes = HorarioSemana.lunesDe(hoy) }) {
                    Text("Hoy", color = VerdeOscuro, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        EncabezadosDias(dias, hoy)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Eje de horas: sin esto las celdas no significan nada.
                Column(
                    modifier = Modifier
                        .width(ANCHO_EJE_HORAS)
                        .padding(start = 12.dp)
                ) {
                    repeat(HorarioSemana.NUM_BLOQUES) { indice ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ALTO_CELDA),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                HorarioSemana.etiquetaHora(indice),
                                fontSize = 11.sp,
                                color = Color.Gray
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

            Spacer(modifier = Modifier.height(16.dp))
        }

        LeyendaHorario()
    }

    // Detalle de una reserva ya existente.
    val reserva = reservaAMostrar
    if (reserva != null) {
        val esMia = reserva.usuarioId == usuarioIdActual
        AlertDialog(
            onDismissRequest = { reservaAMostrar = null },
            title = { Text(reserva.titulo) },
            text = {
                Column {
                    Text("${reserva.fecha.etiquetaLarga()}")
                    Text(reserva.rangoDeHoras())
                    if (!reserva.detalles.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(reserva.detalles)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (esMia) "Reservación tuya" else "Reservado por otro usuario",
                        fontSize = 12.sp,
                        color = Color.Gray
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
                            viewModel.eliminar(reserva)
                            reservaAMostrar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                }
            } else {
                null
            }
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
                    .padding(horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            if (esHoy) ColumnaHoy else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        HorarioSemana.ENCABEZADOS[indice],
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esHoy) VerdeOscuro else Color.Gray
                    )
                    Text(
                        "${fecha.dayOfMonth}",
                        fontSize = 10.sp,
                        color = if (esHoy) VerdeOscuro else Color.Gray
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
    val horaInicio = HorarioSemana.inicioDeBloque(indiceBloque)

    // El título aparece solo en el bloque donde arranca la reserva, para no
    // repetirlo cinco veces si ocupa varias horas seguidas.
    val muestraTitulo = reserva != null && reserva.horaInicio >= horaInicio

    val descripcion = if (reserva == null) {
        "Libre, ${fecha.etiquetaLarga()} ${HorarioSemana.etiquetaHora(indiceBloque)}"
    } else {
        "Reservado: ${reserva.titulo}, ${reserva.rangoDeHoras()}"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ALTO_CELDA)
            .padding(vertical = 1.dp)
            .background(
                if (reserva == null) CeldaLibre else CeldaReservada,
                RoundedCornerShape(6.dp)
            )
            .border(1.dp, CeldaBorde.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .clickable {
                if (reserva == null) onCeldaLibreClick() else onCeldaReservadaClick(reserva)
            }
            .semantics { contentDescription = descripcion },
        contentAlignment = Alignment.Center
    ) {
        if (muestraTitulo) {
            Text(
                reserva!!.titulo,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
private fun LeyendaHorario() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemLeyenda(color = CeldaLibre, texto = "Libre")
        ItemLeyenda(color = CeldaReservada, texto = "Reservado")
    }
}

@Composable
private fun ItemLeyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, fontSize = 12.sp, color = Color.Gray)
    }
}

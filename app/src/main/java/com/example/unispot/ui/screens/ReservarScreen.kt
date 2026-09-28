package com.example.unispot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.CategoriaReserva
import com.example.unispot.data.ReservacionRepository
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.ui.HorarioSemana
import com.example.unispot.ui.ReservaPendiente
import com.example.unispot.ui.components.AreaTactilMinima
import com.example.unispot.ui.components.MuestraCategoria
import com.example.unispot.ui.etiquetaLarga
import com.example.unispot.ui.theme.UniSpotTheme
import com.example.unispot.ui.theme.color
import com.example.unispot.ui.theme.colorBorde
import com.example.unispot.ui.theme.colorContenido
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservarScreen(
    pendiente: ReservaPendiente?,
    usuarioId: Long,
    viewModel: ReservacionViewModel,
    onGuardada: () -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var detalles by remember { mutableStateOf("") }

    // La categoría decide el color de la celda en el calendario. Se elige
    // siempre una, sin obligar a tocar nada: si se olvida, se publica como
    // clase, que es el caso más común.
    var categoria by remember { mutableStateOf(CategoriaReserva.POR_DEFECTO) }

    // Si se llegó desde una celda del calendario, el formulario queda precargado.
    var fecha by remember(pendiente) { mutableStateOf(pendiente?.fecha ?: LocalDate.now()) }
    var horaInicio by remember(pendiente) { mutableStateOf(pendiente?.horaInicio ?: "10:00") }
    var horaFin by remember(pendiente) { mutableStateOf(pendiente?.horaFin ?: "11:00") }

    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var editandoInicio by remember { mutableStateOf(false) }
    var editandoFin by remember { mutableStateOf(false) }

    val error by viewModel.errorReserva.collectAsStateWithLifecycle()
    val reservaGuardada by viewModel.ultimaReservaGuardadaId.collectAsStateWithLifecycle()

    LaunchedEffect(pendiente) { viewModel.limpiarErrorReserva() }

    // Solo se sale del formulario cuando el alta se completó de verdad; si hay
    // choque de horario, el error se muestra aquí y la pantalla se mantiene.
    LaunchedEffect(reservaGuardada) {
        if (reservaGuardada != null) {
            viewModel.limpiarReservaGuardada()
            onGuardada()
        }
    }

    // El horario del formulario tiene que caer dentro de la rejilla (07-21),
    // si no la reserva quedaría fuera de la vista del calendario.
    val fueraDeHorario = remember(horaInicio, horaFin) {
        val inicio = ReservacionRepository.aMinutos(horaInicio)
        val fin = ReservacionRepository.aMinutos(horaFin)
        inicio < HorarioSemana.HORA_INICIO * 60 ||
            fin > HorarioSemana.HORA_FIN * 60 ||
            inicio >= fin
    }

    // Limitado y centrado: en tablet el formulario no debe estirarse de borde
    // a borde, las líneas se vuelven ilegibles.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                "Nueva Reservación",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = UniSpotTheme.colors.verdeOscuro,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; viewModel.limpiarErrorReserva() },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = detalles,
                onValueChange = { detalles = it },
                label = { Text("Descripción (opcional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            SelectorCategoria(
                seleccionada = categoria,
                onSeleccionar = { categoria = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                pendiente?.let { "${it.edificioNombre} - ${it.aulaNombre}" } ?: "Selecciona un aula",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            SelectorCampo(
                etiqueta = "Fecha",
                valor = fecha.etiquetaLarga(),
                icono = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                onClick = { mostrarSelectorFecha = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SelectorCampo(
                    etiqueta = "Hora inicio",
                    valor = horaInicio,
                    icono = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    onClick = { editandoInicio = true },
                    modifier = Modifier.weight(1f)
                )

                SelectorCampo(
                    etiqueta = "Hora fin",
                    valor = horaFin,
                    icono = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    onClick = { editandoFin = true },
                    modifier = Modifier.weight(1f)
                )
            }

            if (fueraDeHorario) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "El horario debe estar entre las 07:00 y las 21:00, y la hora de fin " +
                        "debe ser posterior a la de inicio.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        viewModel.reservar(
                            usuarioId = usuarioId,
                            aulaId = pendiente?.aulaId ?: -1,
                            titulo = titulo,
                            detalles = detalles,
                            fecha = fecha,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            categoria = categoria
                        )
                    },
                    enabled = titulo.isNotBlank() && !fueraDeHorario,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UniSpotTheme.colors.morado,
                        // El morado sí aguanta texto blanco (15.5:1), a diferencia de
                        // los verdes.
                        contentColor = Color.White
                    )
                ) {
                    Text("Reservar")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (mostrarSelectorFecha) {
        val estadoFecha = rememberDatePickerState(
            initialSelectedDateMillis = fecha.atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let { milis ->
                        // El selector entrega UTC, por eso se lee en esa misma zona.
                        fecha = Instant.ofEpochMilli(milis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    mostrarSelectorFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorFecha = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }

    if (editandoInicio || editandoFin) {
        val esInicio = editandoInicio
        val valorActual = if (esInicio) horaInicio else horaFin
        val partes = valorActual.split(":")
        val estadoHora = rememberTimePickerState(
            initialHour = partes[0].toIntOrNull() ?: 10,
            initialMinute = partes.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = {
                editandoInicio = false
                editandoFin = false
            },
            title = { Text(if (esInicio) "Hora de inicio" else "Hora de fin") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimePicker(state = estadoHora)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val nueva = "%02d:%02d".format(estadoHora.hour, estadoHora.minute)
                    if (esInicio) horaInicio = nueva else horaFin = nueva
                    viewModel.limpiarErrorReserva()
                    editandoInicio = false
                    editandoFin = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    editandoInicio = false
                    editandoFin = false
                }) { Text("Cancelar") }
            }
        )
    }
}

/**
 * Selector de categoría, con los cinco chips y "Clases" marcada de entrada.
 *
 * Se muestran los nombres, no solo los colores: los cinco tonos se parecen
 * demasiado para distinguirlos a simple vista, así que la muestra sola no
 * bastaría. Al elegir, el chip toma el color de la categoría, que es
 * exactamente como se verá en el calendario.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectorCategoria(
    seleccionada: CategoriaReserva,
    onSeleccionar: (CategoriaReserva) -> Unit
) {
    Column {
        Text(
            "Categoría",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Define el color con el que se verá en el calendario.",
            fontSize = 12.sp,
            color = UniSpotTheme.colors.textoSecundario
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoriaReserva.entries.forEach { opcion ->
                ChipCategoria(
                    categoria = opcion,
                    seleccionada = opcion == seleccionada,
                    onClick = { onSeleccionar(opcion) }
                )
            }
        }
    }
}

@Composable
private fun ChipCategoria(
    categoria: CategoriaReserva,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(999.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            // selectable y no clickable: así TalkBack anuncia si está marcado,
            // que es lo único que distingue un chip de otro cuando el color es
            // la única diferencia visual.
            .selectable(
                selected = seleccionada,
                role = Role.RadioButton,
                onClick = onClick
            )
            .heightIn(min = AreaTactilMinima)
            .background(
                if (seleccionada) categoria.color else MaterialTheme.colorScheme.surface,
                forma
            )
            .border(1.dp, categoria.colorBorde, forma)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        if (!seleccionada) {
            MuestraCategoria(categoria = categoria)
        }

        Text(
            categoria.nombre,
            fontSize = 13.sp,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionada) {
                categoria.colorContenido
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun SelectorCampo(
    etiqueta: String,
    valor: String,
    icono: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icono()
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    etiqueta,
                    fontSize = 11.sp,
                    color = UniSpotTheme.colors.textoSecundario
                )
                Text(
                    valor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

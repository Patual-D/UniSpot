package com.example.unispot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.data.ReservacionEntity
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.ui.ContenidoCentrado
import com.example.unispot.ui.components.ConfirmacionBorrado
import com.example.unispot.ui.components.TarjetaReserva
import com.example.unispot.ui.components.TituloPantalla
import com.example.unispot.ui.etiqueta
import com.example.unispot.ui.etiquetaLarga
import com.example.unispot.ui.theme.UniSpotTheme

/**
 * Reservas del usuario en sesión. El listado sale de la base de datos a través
 * del Flow del ViewModel, así que refleja también lo que se acaba de guardar.
 */
@Composable
fun MisReservasScreen(
    viewModel: ReservacionViewModel,
    catalogo: CatalogoViewModel,
    modifier: Modifier = Modifier
) {
    val reservas by viewModel.misReservaciones.collectAsStateWithLifecycle(emptyList())
    val aulasPorId by catalogo.aulasPorId.collectAsStateWithLifecycle()

    // El borrado no se puede deshacer, así que siempre se confirma primero.
    var aEliminar by remember { mutableStateOf<ReservacionEntity?>(null) }

    ContenidoCentrado(
        anchoMaximo = 600.dp,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TituloPantalla("Mis Reservaciones")

            Spacer(modifier = Modifier.heightIn(min = 16.dp))

            if (reservas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Todavía no tienes reservaciones.",
                        color = UniSpotTheme.colors.textoSecundario,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.heightIn(min = 6.dp))
                    Text(
                        "Elige un edificio, un aula y toca un horario libre.",
                        color = UniSpotTheme.colors.textoSecundario,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(reservas, key = { it.reserva.id }) { conUsuario ->
                        val reserva = conUsuario.reserva
                        TarjetaReserva(
                            reserva = reserva,
                            nombreAula = aulasPorId[reserva.aulaId]?.let {
                                "${it.edificio.nombre} - ${it.aula.nombre}"
                            } ?: "Aula eliminada",
                            onEliminar = { aEliminar = reserva }
                        )
                    }
                }
            }
        }
    }

    aEliminar?.let { reserva ->
        ConfirmacionBorrado(
            mensaje = "Vas a eliminar la reservación \"${reserva.titulo}\" del " +
                "${reserva.fecha.etiquetaLarga()}, de ${reserva.horaInicio.etiqueta()} a " +
                "${reserva.horaFin.etiqueta()}. Esta acción no se puede deshacer.",
            onConfirmar = {
                viewModel.eliminar(reserva)
                aEliminar = null
            },
            onCancelar = { aEliminar = null }
        )
    }
}

package com.example.unispot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.ui.components.TarjetaReserva
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.VerdeOscuro

/**
 * Reservas del usuario en sesión. El listado sale de la base de datos a través
 * del Flow del ViewModel, así que refleja también lo que se acaba de guardar.
 */
@Composable
fun MisReservasScreen(
    viewModel: ReservacionViewModel,
    catalogo: CatalogoViewModel
) {
    val reservas by viewModel.misReservaciones.collectAsStateWithLifecycle(emptyList())
    val aulasPorId by catalogo.aulasPorId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            "Mis Reservaciones",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuro
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (reservas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Todavía no tienes reservaciones.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Elige un edificio, un aula y toca un horario libre.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reservas, key = { it.id }) { reserva ->
                    TarjetaReserva(
                        reserva = reserva,
                        nombreAula = aulasPorId[reserva.aulaId]?.let {
                            "${it.edificio.nombre} - ${it.aula.nombre}"
                        } ?: "Aula eliminada",
                        onEliminar = { viewModel.eliminar(reserva) }
                    )
                }
            }
        }
    }
}

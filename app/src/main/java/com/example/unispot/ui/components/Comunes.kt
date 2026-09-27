package com.example.unispot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unispot.R
import com.example.unispot.data.ReservacionEntity
import com.example.unispot.ui.Pantalla
import com.example.unispot.ui.etiquetaLarga
import com.example.unispot.ui.rangoDeHoras
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.VerdeOscuro
import com.example.unispot.ui.theme.VerdePrincipal

@Composable
fun LogoUniSpot(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.unispot_logo),
        contentDescription = "Logo UniSpot",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    contenedor: Color = VerdePrincipal
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = contenedor,
            contentColor = Color.White,
            disabledContainerColor = contenedor.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Text(texto)
    }
}

/** Fila de la lista de edificios. */
@Composable
fun BotonLista(texto: String, subtitulo: String? = null, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(VerdePrincipal, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    texto,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (subtitulo != null) {
                    Text(
                        subtitulo,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Ir",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    texto: String,
    logoRes: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(texto, fontSize = 14.sp)
        }
    }
}

@Composable
fun PantallaBase(
    pantallaActual: Pantalla,
    onInicio: () -> Unit,
    onReservas: () -> Unit,
    onPerfil: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Fondo),
            contentAlignment = Alignment.Center
        ) {
            LogoUniSpot(
                modifier = Modifier
                    .width(140.dp)
                    .height(55.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            contenido()
        }

        BarraInferior(
            pantallaActual = pantallaActual,
            onInicio = onInicio,
            onReservas = onReservas,
            onPerfil = onPerfil
        )
    }
}

@Composable
private fun BarraInferior(
    pantallaActual: Pantalla,
    onInicio: () -> Unit,
    onReservas: () -> Unit,
    onPerfil: () -> Unit
) {
    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = Color.White,
        unselectedIconColor = Color.White,
        unselectedTextColor = Color.White,
        indicatorColor = VerdeOscuro
    )

    NavigationBar(
        containerColor = VerdePrincipal,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = pantallaActual.esDeInicio(),
            onClick = onInicio,
            icon = { Icon(Icons.Default.Apartment, contentDescription = "Edificios") },
            label = { Text("Edificios") },
            colors = colores
        )

        NavigationBarItem(
            selected = pantallaActual == Pantalla.RESERVAS,
            onClick = onReservas,
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Reservas") },
            label = { Text("Reservas") },
            colors = colores
        )

        NavigationBarItem(
            selected = pantallaActual == Pantalla.PERFIL,
            onClick = onPerfil,
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = colores
        )
    }
}

fun Pantalla.esDeInicio(): Boolean = this == Pantalla.EDIFICIOS ||
    this == Pantalla.SALONES ||
    this == Pantalla.HORARIO ||
    this == Pantalla.RESERVAR

/** Tarjeta de una reserva, con deslizamiento a la izquierda para eliminarla. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaReserva(
    reserva: ReservacionEntity,
    nombreAula: String,
    onEliminar: () -> Unit
) {
    val estadoDeslizar = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.EndToStart) {
                onEliminar()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = estadoDeslizar,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red, RoundedCornerShape(15.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar reservación",
                    tint = Color.White
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            color = VerdePrincipal
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        reserva.titulo,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        nombreAula,
                        color = Color.White
                    )

                    Text(
                        "${reserva.fecha.etiquetaLarga()} · ${reserva.rangoDeHoras()}",
                        color = Color.White
                    )

                    if (!reserva.detalles.isNullOrBlank()) {
                        Text(
                            reserva.detalles,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar reservación",
                    tint = Color.White
                )
            }
        }
    }
}

/** Iniciales del usuario, para el perfil cuando no hay foto. */
@Composable
fun InicialesUsuario(nombre: String, modifier: Modifier = Modifier) {
    val iniciales = nombre.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(VerdeOscuro),
        contentAlignment = Alignment.Center
    ) {
        Text(
            iniciales,
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

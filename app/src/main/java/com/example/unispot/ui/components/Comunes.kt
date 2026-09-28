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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unispot.R
import com.example.unispot.data.ReservacionEntity
import com.example.unispot.ui.Pantalla
import com.example.unispot.ui.anchoDeVentana
import com.example.unispot.ui.etiquetaLarga
import com.example.unispot.ui.rangoDeHoras
import com.example.unispot.ui.theme.UniSpotTheme

/** Área táctil mínima que recommends Material. */
val AreaTactilMinima = 48.dp

@Composable
fun LogoUniSpot(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.unispot_logo),
        contentDescription = null,
        modifier = modifier.clearAndSetSemantics {},
        contentScale = ContentScale.Fit
    )
}

@Composable
fun TituloPantalla(
    texto: String,
    modifier: Modifier = Modifier
) {
    Text(
        texto,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        // Sin esto TalkBack no reconoce los títulos de sección al navegar.
        modifier = modifier.semantics { heading() }
    )
}

@Composable
fun TextoSecundario(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        color = UniSpotTheme.colors.textoSecundario,
        fontSize = 13.sp,
        modifier = modifier
    )
}

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    contenedor: Color = UniSpotTheme.colors.verdePrincipal
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AreaTactilMinima + 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = contenedor,
            contentColor = contenidoDe(contenedor),
            disabledContainerColor = contenedor.copy(alpha = 0.4f),
            disabledContentColor = contenidoDe(contenedor).copy(alpha = 0.7f)
        )
    ) {
        Text(texto)
    }
}

/**
 * El morado es claro y oscuro, así que ahí el contenido blanco sí funciona
 * (15.5:1). En los verdes el blanco se queda en 2.62:1, así que se usa un
 * verde muy oscuro (6.18:1). Los verdes de marca no se tocan.
 */
@Composable
private fun contenidoDe(fondo: Color): Color = when (fondo) {
    UniSpotTheme.colors.morado -> Color.White
    UniSpotTheme.colors.verdeOscuro -> UniSpotTheme.colors.contenidoSobreVerdeOscuro
    else -> UniSpotTheme.colors.contenidoSobreVerde
}

/**
 * Fila de lista. En el panel lateral de tablet hace falta distinguir lo que ya
 * está elegido, así que la fila seleccionada pasa al verde oscuro, que además
 * ya tenía 7.44:1 de contraste con el blanco.
 */
@Composable
fun BotonLista(
    texto: String,
    subtitulo: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    seleccionado: Boolean = false
) {
    val colores = UniSpotTheme.colors
    val contenido = if (seleccionado) {
        colores.contenidoSobreVerdeOscuro
    } else {
        colores.contenidoSobreVerde
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AreaTactilMinima)
            .padding(vertical = 6.dp)
            .background(
                if (seleccionado) colores.verdeOscuro else colores.verdePrincipal,
                RoundedCornerShape(15.dp)
            )
            .clickable(onClick = onClick)
            .padding(18.dp)
            .semantics {
                if (subtitulo != null) {
                    contentDescription = "$texto, $subtitulo"
                }
                if (seleccionado) {
                    selected = true
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                texto,
                color = contenido,
                fontWeight = FontWeight.Bold
            )
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    color = contenido,
                    fontSize = 13.sp
                )
            }
        }

        if (seleccionado) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = contenido,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = contenido,
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
            .heightIn(min = AreaTactilMinima + 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            // En tema claro surface ya es blanco, como los botones de Google y
            // Microsoft. En oscuro se evita un rectángulo blancoIterable.
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
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

/**
 * Andamiaje común. En pantallas medianas o grandes la navegación pasa a una
 * barra lateral: en vertical una barra inferior de 80dp roba mucho alto
 * útil, y en horizontal hay sitio de sobra a los lados.
 */
@Composable
fun PantallaBase(
    pantallaActual: Pantalla,
    onInicio: () -> Unit,
    onReservas: () -> Unit,
    onPerfil: () -> Unit,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    val ancho = anchoDeVentana()
    val colores = UniSpotTheme.colors
    val fondo = MaterialTheme.colorScheme.background

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(fondo)
    ) {
        if (ancho.esMedianaOMayor) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .statusBarsPadding()
            ) {
                LogoUniSpot(
                    modifier = Modifier
                        .padding(16.dp)
                        .width(120.dp)
                        .height(48.dp)
                )
                BarraLateral(
                    pantallaActual = pantallaActual,
                    onInicio = onInicio,
                    onReservas = onReservas,
                    onPerfil = onPerfil
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .statusBarsPadding()
                // La barra inferior ya aplica su propio recorte, así que solo
                // hay que reservarlo cuando no existe: en tablet, si no, el
                // contenido queda debajo del gesto de volver.
                .then(
                    if (ancho.esCompacta) {
                        Modifier
                    } else {
                        Modifier.navigationBarsPadding()
                    }
                )
        ) {
            if (ancho.esCompacta) {
                EncabezadoLogo()
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                contenido()
            }

            if (ancho.esCompacta) {
                BarraInferior(
                    pantallaActual = pantallaActual,
                    onInicio = onInicio,
                    onReservas = onReservas,
                    onPerfil = onPerfil
                )
            }
        }
    }
}

@Composable
private fun EncabezadoLogo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        LogoUniSpot(
            modifier = Modifier
                .width(140.dp)
                .height(55.dp)
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
    val colores = UniSpotTheme.colors

    // Sin navigationBarsPadding: la barra ya aplica su propio recorte por
    // defecto y aplicarlo dos veces dejaba un hueco de más abajo.
    NavigationBar(
        containerColor = colores.verdePrincipal
    ) {
        NavigationBarItem(
            selected = pantallaActual.esDeInicio(),
            onClick = onInicio,
            icon = {
                Icon(Icons.Default.Apartment, contentDescription = null)
            },
            label = { Text("Edificios") },
            colors = coloresNavegacion()
        )

        NavigationBarItem(
            selected = pantallaActual == Pantalla.RESERVAS,
            onClick = onReservas,
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Reservas") },
            colors = coloresNavegacion()
        )

        NavigationBarItem(
            selected = pantallaActual == Pantalla.PERFIL,
            onClick = onPerfil,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = coloresNavegacion()
        )
    }
}

@Composable
private fun BarraLateral(
    pantallaActual: Pantalla,
    onInicio: () -> Unit,
    onReservas: () -> Unit,
    onPerfil: () -> Unit
) {
    val colores = UniSpotTheme.colors

    NavigationRail(
        containerColor = colores.verdePrincipal,
        modifier = Modifier.width(96.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        NavigationRailItem(
            selected = pantallaActual.esDeInicio(),
            onClick = onInicio,
            icon = { Icon(Icons.Default.Apartment, contentDescription = null) },
            label = { Text("Edificios") },
            colors = coloresNavegacionRail()
        )

        NavigationRailItem(
            selected = pantallaActual == Pantalla.RESERVAS,
            onClick = onReservas,
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Reservas") },
            colors = coloresNavegacionRail()
        )

        NavigationRailItem(
            selected = pantallaActual == Pantalla.PERFIL,
            onClick = onPerfil,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = coloresNavegacionRail()
        )
    }
}

@Composable
private fun coloresNavegacion() = NavigationBarItemDefaults.colors(
    selectedIconColor = UniSpotTheme.colors.contenidoSobreVerdeOscuro,
    selectedTextColor = UniSpotTheme.colors.contenidoSobreVerde,
    unselectedIconColor = UniSpotTheme.colors.contenidoSobreVerde,
    unselectedTextColor = UniSpotTheme.colors.contenidoSobreVerde,
    indicatorColor = UniSpotTheme.colors.verdeOscuro
)

@Composable
private fun coloresNavegacionRail() = NavigationRailItemDefaults.colors(
    selectedIconColor = UniSpotTheme.colors.contenidoSobreVerdeOscuro,
    selectedTextColor = UniSpotTheme.colors.contenidoSobreVerde,
    unselectedIconColor = UniSpotTheme.colors.contenidoSobreVerde,
    unselectedTextColor = UniSpotTheme.colors.contenidoSobreVerde,
    indicatorColor = UniSpotTheme.colors.verdeOscuro
)

fun Pantalla.esDeInicio(): Boolean = this == Pantalla.EDIFICIOS ||
    this == Pantalla.SALONES ||
    this == Pantalla.HORARIO ||
    this == Pantalla.RESERVAR

/**
 * Tarjeta de una reserva. Se puede eliminar deslizando o con el botón, y en
 * ambos casos se pide confirmación: el borrado no se puede deshacer y un
 * deslizamiento es fácil ejecutar sin querer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaReserva(
    reserva: ReservacionEntity,
    nombreAula: String,
    onEliminar: () -> Unit
) {
    val colores = UniSpotTheme.colors
    val estadoDeslizar = rememberSwipeToDismissBoxState()

    // El deslizamiento no borra: solo dispara la misma confirmación que el
    // botón, y la tarjeta vuelve a su sitio. Sin esto un gesto rápido perdía la
    // reserva sin haber confirmado nada.
    LaunchedEffect(estadoDeslizar.currentValue) {
        if (estadoDeslizar.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onEliminar()
            estadoDeslizar.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = estadoDeslizar,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(15.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            color = colores.verdePrincipal
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        reserva.titulo,
                        color = colores.contenidoSobreVerde,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // La categoría va en la tarjeta porque es lo que explica el
                    // color con el que aparece en el calendario.
                    InsigniaCategoria(categoria = reserva.categoria)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        nombreAula,
                        color = colores.contenidoSobreVerde
                    )

                    Text(
                        "${reserva.fecha.etiquetaLarga()} · ${reserva.rangoDeHoras()}",
                        color = colores.contenidoSobreVerde
                    )

                    if (!reserva.detalles.isNullOrBlank()) {
                        Text(
                            reserva.detalles,
                            color = colores.contenidoSobreVerde,
                            fontSize = 13.sp
                        )
                    }
                }

                // Este icono antes no tenía onClick: se veía pero no hacía nada.
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(AreaTactilMinima)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar la reservación ${reserva.titulo}",
                        tint = colores.contenidoSobreVerde
                    )
                }
            }
        }
    }
}

/** Diálogo de confirmación para acciones destructivas. */
@Composable
fun ConfirmacionBorrado(
    titulo: String = "Eliminar reservación",
    mensaje: String,
    textoConfirmar: String = "Eliminar",
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, modifier = Modifier.semantics { heading() }) },
        text = { Text(mensaje) },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(textoConfirmar)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

/** Iniciales del usuario, como alternativa a la foto. */
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
            .background(UniSpotTheme.colors.verdeOscuro),
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

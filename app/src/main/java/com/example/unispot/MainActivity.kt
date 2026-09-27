package com.example.unispot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.data.ReservacionViewModel
import com.example.unispot.data.SesionViewModel
import com.example.unispot.data.UsuarioEntity
import com.example.unispot.ui.ControladorNavegacion
import com.example.unispot.ui.Pantalla
import com.example.unispot.ui.ReservaPendiente
import com.example.unispot.ui.components.PantallaBase
import com.example.unispot.ui.screens.EdificiosScreen
import com.example.unispot.ui.screens.HorarioScreen
import com.example.unispot.ui.screens.LoginScreen
import com.example.unispot.ui.screens.MisReservasScreen
import com.example.unispot.ui.screens.PantallaCarga
import com.example.unispot.ui.screens.PerfilScreen
import com.example.unispot.ui.screens.RegistroScreen
import com.example.unispot.ui.screens.ReservarScreen
import com.example.unispot.ui.screens.SalonesScreen
import com.example.unispot.ui.theme.Fondo
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Fondo) {
                    UniSpotApp()
                }
            }
        }
    }
}

@Composable
fun UniSpotApp() {
    val sesion: SesionViewModel = viewModel()
    val catalogo: CatalogoViewModel = viewModel()
    val reservaciones: ReservacionViewModel = viewModel()

    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val restaurando by sesion.restaurando.collectAsStateWithLifecycle()

    val controlador = remember { ControladorNavegacion() }
    var servicioNoDisponible by remember { mutableStateOf<String?>(null) }

    // Las listas de reservas se filtran por el usuario de la sesión.
    LaunchedEffect(usuario?.id) {
        reservaciones.establecerUsuario(usuario?.id ?: -1L)
    }

    // Al entrar o registrarse se pasa directo a la lista de edificios.
    LaunchedEffect(usuario?.id) {
        if (usuario != null && controlador.pantalla in ACCESO) {
            controlador.irA(Pantalla.EDIFICIOS)
        }
    }

    BackHandler(enabled = controlador.puedeVolver()) {
        controlador.volver()
    }

    if (restaurando) {
        PantallaCarga()
        return
    }

    if (usuario == null) {
        PantallaAcceso(
            sesion = sesion,
            enRegistro = controlador.pantalla == Pantalla.REGISTRO,
            onIrARegistro = { controlador.irA(Pantalla.REGISTRO) },
            onVolverDeRegistro = { controlador.irA(Pantalla.LOGIN) },
            servicioNoDisponible = servicioNoDisponible,
            onServicioNoDisponible = { servicioNoDisponible = it },
            onCerrarAviso = { servicioNoDisponible = null }
        )
        return
    }

    ContenidoSesionIniciada(
        usuario = usuario!!,
        catalogo = catalogo,
        reservaciones = reservaciones,
        controlador = controlador,
        onCerrarSesion = { sesion.cerrarSesion() }
    )
}

private val ACCESO = setOf(Pantalla.LOGIN, Pantalla.REGISTRO)

@Composable
private fun PantallaAcceso(
    sesion: SesionViewModel,
    enRegistro: Boolean,
    onIrARegistro: () -> Unit,
    onVolverDeRegistro: () -> Unit,
    servicioNoDisponible: String?,
    onServicioNoDisponible: (String) -> Unit,
    onCerrarAviso: () -> Unit
) {
    if (enRegistro) {
        RegistroScreen(sesion = sesion, onVolver = onVolverDeRegistro)
    } else {
        LoginScreen(
            sesion = sesion,
            onIrARegistro = onIrARegistro,
            onSocialNoDisponible = onServicioNoDisponible
        )
    }

    servicioNoDisponible?.let { servicio ->
        AlertDialog(
            onDismissRequest = onCerrarAviso,
            title = { Text("Ingreso con $servicio") },
            text = {
                Text(
                    "El acceso con $servicio todavía no está disponible. " +
                        "Por ahora usa tu correo y contraseña."
                )
            },
            confirmButton = {
                TextButton(onClick = onCerrarAviso) { Text("Entendido") }
            }
        )
    }
}

@Composable
private fun ContenidoSesionIniciada(
    usuario: UsuarioEntity,
    catalogo: CatalogoViewModel,
    reservaciones: ReservacionViewModel,
    controlador: ControladorNavegacion,
    onCerrarSesion: () -> Unit
) {
    // Si se pierde el edificio o el aula (por ejemplo tras cerrar sesión y
    // volver a entrar) se regresa a la raíz en vez de mostrar datos vacíos.
    LaunchedEffect(controlador.pantalla, controlador.edificio, controlador.aula) {
        val necesitaAula = controlador.pantalla in setOf(Pantalla.HORARIO, Pantalla.RESERVAR)
        if ((controlador.pantalla == Pantalla.SALONES && controlador.edificio == null) ||
            (necesitaAula && (controlador.aula == null || controlador.edificio == null))
        ) {
            controlador.irA(Pantalla.EDIFICIOS)
        }
    }

    val edificio = controlador.edificio
    val aula = controlador.aula

    PantallaBase(
        pantallaActual = controlador.pantalla,
        onInicio = { controlador.irA(Pantalla.EDIFICIOS) },
        onReservas = { controlador.irA(Pantalla.RESERVAS) },
        onPerfil = { controlador.irA(Pantalla.PERFIL) }
    ) {
        when (controlador.pantalla) {
            Pantalla.EDIFICIOS -> EdificiosScreen(
                catalogo = catalogo,
                onEdificioClick = { controlador.elegirEdificio(it) }
            )

            Pantalla.SALONES -> if (edificio != null) {
                SalonesScreen(
                    catalogo = catalogo,
                    edificioId = edificio.id,
                    edificioNombre = edificio.nombre,
                    onSalonClick = { controlador.elegirAula(it) }
                )
            }

            Pantalla.HORARIO -> if (edificio != null && aula != null) {
                HorarioScreen(
                    aulaId = aula.id,
                    aulaNombre = aula.nombre,
                    edificioNombre = edificio.nombre,
                    usuarioIdActual = usuario.id,
                    viewModel = reservaciones,
                    onReservar = { controlador.prepararReserva(it) }
                )
            }

            Pantalla.RESERVAR -> if (edificio != null && aula != null) {
                ReservarScreen(
                    pendiente = controlador.reservaPendiente ?: ReservaPendiente(
                        aulaId = aula.id,
                        aulaNombre = aula.nombre,
                        edificioNombre = edificio.nombre,
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
            }

            Pantalla.RESERVAS -> MisReservasScreen(
                viewModel = reservaciones,
                catalogo = catalogo
            )

            Pantalla.PERFIL -> PerfilScreen(
                usuario = usuario,
                onCerrarSesion = onCerrarSesion
            )

            Pantalla.LOGIN, Pantalla.REGISTRO -> Unit
        }
    }
}

package com.example.unispot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.SesionViewModel
import com.example.unispot.data.ValidacionUsuario
import com.example.unispot.ui.ContenidoCentrado
import com.example.unispot.ui.components.BotonPrincipal
import com.example.unispot.ui.components.LogoUniSpot
import com.example.unispot.ui.theme.UniSpotTheme

/**
 * Alta de cuenta con los cuatro datos que pide el prototipo: matrícula, nombre,
 * correo y contraseña. Al terminar, la cuenta queda iniciada automáticamente.
 */
@Composable
fun RegistroScreen(
    sesion: SesionViewModel,
    onVolver: () -> Unit
) {
    var matricula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }

    val error by sesion.mensajeError.collectAsStateWithLifecycle()
    val procesando by sesion.procesando.collectAsStateWithLifecycle()

    LaunchedEffect(matricula, nombre, correo, contrasena, confirmacion) { sesion.limpiarError() }

    // Pistas de formato en vivo, sin llegar a la base de datos.
    val errorMatricula = ValidacionUsuario.errorMatricula(matricula)
    val errorNombre = ValidacionUsuario.errorNombre(nombre)
    val errorCorreo = ValidacionUsuario.errorCorreo(correo)
    val errorContrasena = ValidacionUsuario.errorContrasena(contrasena)
    val errorConfirmacion = if (confirmacion.isBlank()) {
        null
    } else {
        ValidacionUsuario.errorConfirmacion(contrasena, confirmacion)
    }

    val formularioValido = listOf(
        matricula, nombre, correo, contrasena, confirmacion
    ).all { it.isNotBlank() } &&
        listOf(
            errorMatricula, errorNombre, errorCorreo, errorContrasena, errorConfirmacion
        ).all { it == null }

    val colores = UniSpotTheme.colors

    ContenidoCentrado(anchoMaximo = 520.dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 28.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onVolver,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al inicio de sesión",
                        tint = colores.verdeOscuro
                    )
                }

                LogoUniSpot(
                    modifier = Modifier
                        .size(width = 110.dp, height = 44.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                "Crear cuenta",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = colores.verdeOscuro,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Registra tu cuenta de estudiante para reservar espacios.",
                color = colores.textoSecundario,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoFormulario(
                valor = matricula,
                onValorCambiado = { matricula = it },
                etiqueta = "Matrícula",
                error = errorMatricula,
                teclado = KeyboardType.Number,
                imeAction = ImeAction.Next
            )

            CampoFormulario(
                valor = nombre,
                onValorCambiado = { nombre = it },
                etiqueta = "Nombre completo",
                error = errorNombre,
                imeAction = ImeAction.Next
            )

            CampoFormulario(
                valor = correo,
                onValorCambiado = { correo = it },
                etiqueta = "Correo",
                error = errorCorreo,
                teclado = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            CampoFormulario(
                valor = contrasena,
                onValorCambiado = { contrasena = it },
                etiqueta = "Contraseña",
                error = errorContrasena,
                teclado = KeyboardType.Password,
                imeAction = ImeAction.Next,
                esContrasena = true,
                verContrasena = verContrasena,
                onAlternarVisibilidad = { verContrasena = !verContrasena }
            )

            CampoFormulario(
                valor = confirmacion,
                onValorCambiado = { confirmacion = it },
                etiqueta = "Confirmar contraseña",
                error = errorConfirmacion,
                teclado = KeyboardType.Password,
                imeAction = ImeAction.Done,
                esContrasena = true,
                verContrasena = verContrasena,
                onAlternarVisibilidad = { verContrasena = !verContrasena }
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Assertive }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonPrincipal(
                texto = if (procesando) "Creando cuenta..." else "Crear cuenta",
                onClick = {
                    sesion.registrarCuenta(matricula, nombre, correo, contrasena, confirmacion)
                },
                habilitado = formularioValido && !procesando
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿Ya tienes cuenta?",
                    color = colores.textoSecundario,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(
                        "Iniciar sesión",
                        color = colores.verdeOscuro,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CampoFormulario(
    valor: String,
    onValorCambiado: (String) -> Unit,
    etiqueta: String,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    esContrasena: Boolean = false,
    verContrasena: Boolean = false,
    onAlternarVisibilidad: () -> Unit = {}
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambiado,
        label = { Text(etiqueta) },
        singleLine = true,
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error, fontSize = 12.sp) }
        } else {
            null
        },
        visualTransformation = if (esContrasena && !verContrasena) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = imeAction),
        trailingIcon = if (esContrasena) {
            {
                IconButton(
                    onClick = onAlternarVisibilidad,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (verContrasena) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (verContrasena) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        }
                    )
                }
            }
        } else {
            null
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

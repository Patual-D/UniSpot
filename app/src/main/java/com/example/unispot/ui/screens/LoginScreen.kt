package com.example.unispot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.R
import com.example.unispot.data.SesionViewModel
import com.example.unispot.ui.components.BotonPrincipal
import com.example.unispot.ui.components.LogoUniSpot
import com.example.unispot.ui.components.SocialLoginButton
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.VerdeOscuro

@Composable
fun LoginScreen(
    sesion: SesionViewModel,
    onIrARegistro: () -> Unit,
    onSocialNoDisponible: (String) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }

    val error by sesion.mensajeError.collectAsStateWithLifecycle()
    val procesando by sesion.procesando.collectAsStateWithLifecycle()

    // El error se limpia al editar, para no quede pegado al campo.
    LaunchedEffect(correo, contrasena) { sesion.limpiarError() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(35.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoUniSpot(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            isError = error != null,
            visualTransformation = if (verContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = { verContrasena = !verContrasena }) {
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
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                error!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        BotonPrincipal(
            texto = if (procesando) "Verificando..." else "Iniciar Sesión",
            onClick = { sesion.iniciarSesion(correo, contrasena) },
            habilitado = !procesando
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta?", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onIrARegistro) {
                Text("Crear cuenta", color = VerdeOscuro, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text("o", color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        SocialLoginButton(
            texto = "Iniciar sesión con Google",
            logoRes = R.drawable.google_logo,
            onClick = { onSocialNoDisponible("Google") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialLoginButton(
            texto = "Iniciar sesión con Microsoft",
            logoRes = R.drawable.microsoft_logo,
            onClick = { onSocialNoDisponible("Microsoft") }
        )
    }
}

/** Mostrado mientras se restaura la sesión guardada al abrir la app. */
@Composable
fun PantallaCarga() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LogoUniSpot(
                modifier = Modifier
                    .size(width = 160.dp, height = 64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(color = VerdeOscuro)
        }
    }
}

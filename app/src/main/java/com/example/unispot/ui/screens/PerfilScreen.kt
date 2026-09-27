package com.example.unispot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.unispot.data.UsuarioEntity
import com.example.unispot.ui.EsPanol
import com.example.unispot.ui.components.BotonPrincipal
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.Morado
import com.example.unispot.ui.theme.VerdeOscuro
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val FORMATO_FECHA_LARGA: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", EsPanol).withZone(ZoneId.systemDefault())

/**
 * Perfil con los datos de la cuenta creada. Antes mostraba "Juan Ramos" y
 * "Matrícula: 123456" escritos en el código.
 */
@Composable
fun PerfilScreen(
    usuario: UsuarioEntity?,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        Image(
            painter = painterResource(id = R.drawable.profile_student),
            contentDescription = "Foto de perfil del estudiante",
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            usuario?.nombre ?: "Estudiante",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuro
        )

        Text(
            usuario?.correo.orEmpty(),
            color = VerdeOscuro.copy(alpha = 0.75f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DatoPerfil("Matrícula", usuario?.matricula ?: "-")
            DatoPerfil("Correo", usuario?.correo ?: "-")
            DatoPerfil(
                "Cuenta creada",
                usuario?.creadoEn?.let { FORMATO_FECHA_LARGA.format(Instant.ofEpochMilli(it)) } ?: "-"
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        BotonPrincipal(
            texto = "Cerrar Sesión",
            onClick = onCerrarSesion,
            contenedor = Morado
        )
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(etiqueta, color = VerdeOscuro.copy(alpha = 0.8f), fontSize = 14.sp)

            Box(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    valor,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro,
                    fontSize = 14.sp
                )
            }
        }
    }
}

package com.example.unispot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unispot.R
import com.example.unispot.data.UsuarioEntity
import com.example.unispot.ui.ContenidoCentrado
import com.example.unispot.ui.EsPanol
import com.example.unispot.ui.components.BotonPrincipal
import com.example.unispot.ui.theme.UniSpotTheme
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
    val colores = UniSpotTheme.colors

    ContenidoCentrado(anchoMaximo = 520.dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp, vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_student),
                contentDescription = null,
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .semantics {
                        contentDescription = "Foto de perfil del estudiante"
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                usuario?.nombre ?: "Estudiante",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )

            Text(
                usuario?.correo.orEmpty(),
                color = colores.textoSecundario,
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
                contenedor = colores.morado
            )
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    val colores = UniSpotTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(etiqueta, color = colores.textoSecundario, fontSize = 14.sp)

            Text(
                valor,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

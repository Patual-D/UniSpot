package com.example.unispot.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.AulaConEdificio
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.data.EdificioEntity
import com.example.unispot.ui.components.BotonLista
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.VerdeOscuro

@Composable
fun EdificiosScreen(
    catalogo: CatalogoViewModel,
    onEdificioClick: (EdificioEntity) -> Unit
) {
    val edificios by catalogo.edificios.collectAsStateWithLifecycle()
    val aulasPorId by catalogo.aulasPorId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            "Edificios",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuro
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(edificios, key = { it.id }) { edificio ->
                val totalAulas = aulasPorId.values.count { it.edificio.id == edificio.id }
                BotonLista(
                    texto = edificio.nombre,
                    subtitulo = "$totalAulas ${if (totalAulas == 1) "espacio" else "espacios"}",
                    onClick = { onEdificioClick(edificio) }
                )
            }
        }
    }
}

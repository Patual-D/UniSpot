package com.example.unispot.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.data.EdificioEntity
import com.example.unispot.ui.components.BotonLista
import com.example.unispot.ui.components.TituloPantalla
import com.example.unispot.ui.theme.UniSpotTheme

@Composable
fun EdificiosScreen(
    catalogo: CatalogoViewModel,
    onEdificioClick: (EdificioEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val edificios by catalogo.edificios.collectAsStateWithLifecycle()
    val aulasPorId by catalogo.aulasPorId.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TituloPantalla("Edificios")

        Spacer(modifier = Modifier.heightIn(min = 20.dp))

        if (edificios.isEmpty()) {
            Text(
                "Cargando edificios...",
                color = UniSpotTheme.colors.textoSecundario,
                modifier = Modifier.semantics { heading() }
            )
        }

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

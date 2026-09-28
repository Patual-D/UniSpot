package com.example.unispot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.AulaEntity
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.ui.AnchoVentana
import com.example.unispot.ui.anchoDeVentana
import com.example.unispot.ui.components.AreaTactilMinima
import com.example.unispot.ui.components.TextoSecundario
import com.example.unispot.ui.components.TituloPantalla
import com.example.unispot.ui.theme.UniSpotTheme
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SalonesScreen(
    catalogo: CatalogoViewModel,
    edificioId: Long,
    edificioNombre: String,
    onSalonClick: (AulaEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val aulas by catalogo.aulasDe(edificioId).collectAsStateWithLifecycle(emptyList())
    val ancho = anchoDeVentana()
    val colores = UniSpotTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TextoSecundario(edificioNombre)

        Spacer(modifier = Modifier.heightIn(min = 10.dp))

        TituloPantalla("Aulas")

        Spacer(modifier = Modifier.heightIn(min = 20.dp))

        // En tablet caben más columnas; en móvil se mantienen dos para que el
        // nombre del aula no se parta.
        val columnas = when (ancho) {
            AnchoVentana.COMPACTA -> GridCells.Fixed(2)
            AnchoVentana.MEDIANA -> GridCells.Adaptive(minSize = 170.dp)
            AnchoVentana.EXPANDIDA -> GridCells.Adaptive(minSize = 200.dp)
        }

        LazyVerticalGrid(
            columns = columnas,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(aulas, key = { it.id }) { aula ->
                val nombre = aula.nombre
                val capacidad = "${aula.capacidad} lugares"
                Button(
                    onClick = { onSalonClick(aula) },
                    modifier = Modifier
                        .heightIn(min = AreaTactilMinima + 28.dp)
                        .semantics {
                            contentDescription = "$nombre, capacidad $capacidad"
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colores.verdeClaro,
                        // Aquí el blanco daba 1.92:1, ilegible.
                        contentColor = colores.contenidoSobreVerdeClaro
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            aula.nombre,
                            fontWeight = FontWeight.Bold,
                            color = colores.contenidoSobreVerdeClaro
                        )
                        Text(
                            capacidad,
                            fontSize = 12.sp,
                            color = colores.contenidoSobreVerdeClaro
                        )
                    }
                }
            }
        }
    }
}

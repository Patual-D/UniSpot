package com.example.unispot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.unispot.data.AulaEntity
import com.example.unispot.data.CatalogoViewModel
import com.example.unispot.ui.theme.Fondo
import com.example.unispot.ui.theme.VerdeClaro
import com.example.unispot.ui.theme.VerdeOscuro

@Composable
fun SalonesScreen(
    catalogo: CatalogoViewModel,
    edificioId: Long,
    edificioNombre: String,
    onSalonClick: (AulaEntity) -> Unit
) {
    val aulas by catalogo.aulasDe(edificioId).collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            edificioNombre,
            fontSize = 18.sp,
            color = VerdeOscuro
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Aulas",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuro
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(aulas, key = { it.id }) { aula ->
                Button(
                    onClick = { onSalonClick(aula) },
                    modifier = Modifier
                        .fillMaxSize()
                        .height(76.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeClaro)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            aula.nombre,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${aula.capacidad} lugares",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

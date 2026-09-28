package com.example.unispot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unispot.data.CategoriaReserva
import com.example.unispot.ui.theme.color
import com.example.unispot.ui.theme.colorBorde
import com.example.unispot.ui.theme.colorContenido

/**
 * Muestra de color de una categoría: el relleno con su borde.
 *
 * El borde no es decorativo. Los cinco rellenos están en el mismo rango de
 * luminosidad y entre sí no llegan al 3:1 que distingue un componente del que
 * tiene al lado, así que el contorno es lo que recorta la muestra.
 */
@Composable
fun MuestraCategoria(
    categoria: CategoriaReserva,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .background(categoria.color, RoundedCornerShape(4.dp))
            .border(1.dp, categoria.colorBorde, RoundedCornerShape(4.dp))
    )
}

/**
 * Etiqueta con el nombre de la categoría sobre su propio color.
 *
 * El texto usa el color de contenido medido para ese relleno, así que siempre
 * llega al 4.5:1. El nombre va también en el `contentDescription` porque la
 * insignia se lee como una sola unidad y sin esto un lector de pantalla solo
 * anunciaría "Talleres" sin contexto.
 */
@Composable
fun InsigniaCategoria(
    categoria: CategoriaReserva,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .background(categoria.color, RoundedCornerShape(999.dp))
            .border(1.dp, categoria.colorBorde, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .semantics { contentDescription = "Categoría: ${categoria.nombre}" }
    ) {
        Text(
            categoria.nombre,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = categoria.colorContenido
        )
    }
}

/**
 * Muestra de color sin texto, para leyenda. Se oculta de TalkBack a propósito:
 * la leyenda es informativa y el calendario ya anuncia la categoría celda por
 * celda; repetirla sería ruido.
 */
@Composable
fun MuestraLeyenda(
    color: Color,
    colorBorde: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clearAndSetSemantics {}
            .size(16.dp)
            .background(color, RoundedCornerShape(4.dp))
            .border(1.dp, colorBorde, RoundedCornerShape(4.dp))
    )
}

package com.example.unispot.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

/**
 * Room no convierte tipos de java.time por sí solo, así que los registramos a
 * mano. La fecha se guarda como epochDay (Long) y la hora como minutos desde
 * medianoche (Int). Guardar la hora como entero permite detectar solapamientos
 * con una simple comparación en SQL.
 */
class Converters {

    @TypeConverter
    fun deLocalDate(valor: LocalDate?): Long? = valor?.toEpochDay()

    @TypeConverter
    fun aLocalDate(valor: Long?): LocalDate? = valor?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun deLocalTime(valor: LocalTime?): Int? = valor?.let { it.toSecondOfDay() / 60 }

    @TypeConverter
    fun aLocalTime(valor: Int?): LocalTime? = valor?.let { LocalTime.ofSecondOfDay(it * 60L) }
}

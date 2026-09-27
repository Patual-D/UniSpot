package com.example.unispot.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UsuarioEntity::class,
        EdificioEntity::class,
        AulaEntity::class,
        ReservacionEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UniSpotDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun edificioDao(): EdificioDao
    abstract fun aulaDao(): AulaDao
    abstract fun reservacionDao(): ReservacionDao

    companion object {
        /**
         * La versión 1 guardaba las reservas en `tabla_reservaciones` con la
         * fecha y las horas como texto, y sin relación con usuarios ni aulas.
         * Ese esquema es incompatible con el nuevo, así que se reconstruye.
         * Las reservas del prototipo anterior se pierden; las cuentas y el
         * catálogo se crean de nuevo.
         */
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `tabla_reservaciones`")

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `usuarios` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`matricula` TEXT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, " +
                        "`correo` TEXT NOT NULL, " +
                        "`contrasenaHash` TEXT NOT NULL, " +
                        "`creadoEn` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_matricula` " +
                        "ON `usuarios` (`matricula`)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` " +
                        "ON `usuarios` (`correo`)"
                )

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `edificios` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombre` TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_edificios_nombre` " +
                        "ON `edificios` (`nombre`)"
                )

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `aulas` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`edificioId` INTEGER NOT NULL, " +
                        "`nombre` TEXT NOT NULL, " +
                        "`capacidad` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`edificioId`) REFERENCES `edificios`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_aulas_edificioId` " +
                        "ON `aulas` (`edificioId`)"
                )

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `reservaciones` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`usuarioId` INTEGER NOT NULL, " +
                        "`aulaId` INTEGER NOT NULL, " +
                        "`titulo` TEXT NOT NULL, " +
                        "`detalles` TEXT, " +
                        "`fecha` INTEGER NOT NULL, " +
                        "`horaInicio` INTEGER NOT NULL, " +
                        "`horaFin` INTEGER NOT NULL, " +
                        "`creadoEn` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE , " +
                        "FOREIGN KEY(`aulaId`) REFERENCES `aulas`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_reservaciones_usuarioId` " +
                        "ON `reservaciones` (`usuarioId`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_reservaciones_aulaId_fecha` " +
                        "ON `reservaciones` (`aulaId`, `fecha`)"
                )
            }
        }

        @Volatile
        private var INSTANCE: UniSpotDatabase? = null

        fun obtenerBaseDeDatos(context: Context): UniSpotDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UniSpotDatabase::class.java,
                    "unispot_database"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

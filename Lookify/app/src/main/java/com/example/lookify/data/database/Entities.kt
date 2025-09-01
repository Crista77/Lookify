package com.example.lookify.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import androidx.room.ForeignKey
import androidx.room.Index

@Entity (tableName = "film")
data class Film(
    @PrimaryKey(autoGenerate = true) val id_film: Int = 0,
    @ColumnInfo(name = "title") val titolo: String,
    @ColumnInfo(name = "number_Cast") val numero_Cast: Int,
    @ColumnInfo(name = "descrption") val descrizione: String,
    @ColumnInfo(name = "duration") val durata: Int,
    @ColumnInfo(name = "category") val categoria: String,
    @ColumnInfo(name = "toSee") val visibile: Boolean,
    @ColumnInfo(name = "image_uri") val imageUri: String? = null,
    @ColumnInfo(name = "visual") var visualizzazioni: Int,
    @ColumnInfo(name = "stars") var stelle: Int = 0
)

@Entity (tableName = "users")
data class Users(
    @PrimaryKey(autoGenerate = true) val id_user: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "name") val nome: String,
    @ColumnInfo(name = "surname") val cognome: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "image") val immagine: String? = null,
    @ColumnInfo(name = "admin") val admin: Boolean = false,
    @ColumnInfo(name = "living") val residenza: String,
    val filmVisti: List<Int> = emptyList(),
    val serieViste: List<Int> = emptyList()
)

@Entity(
    tableName = "film_watched",
    primaryKeys = ["utente_id", "film_id"],
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["id_user"],
            childColumns = ["utente_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Film::class,
            parentColumns = ["id_film"],
            childColumns = ["film_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["utente_id"]), Index(value = ["film_id"])]
)
data class FilmWatched(
    @ColumnInfo(name = "utente_id") val utenteId: Int,
    @ColumnInfo(name = "film_id") val filmId: Int
)

@Entity(tableName = "film_request")
data class FilmRequest(
    @PrimaryKey(autoGenerate = true) val id_request: Int = 0,
    @ColumnInfo(name = "film_id") val filmId: Int,
    @ColumnInfo(name = "richiedente_id") val richiedenteId: Int,
    @ColumnInfo(name = "approvatore_id") val approvatoreId: Int,
    @ColumnInfo(name = "approvato") val approvato: Boolean
)

@Entity (tableName = "serie_tv")
data class SerieTV(
    @PrimaryKey(autoGenerate = true) val id_serie: Int = 0,
    @ColumnInfo(name = "title") val titolo: String,
    @ColumnInfo(name = "number_Cast") val numero_Cast: Int,
    @ColumnInfo(name = "descrption") val descrizione: String,
    @ColumnInfo(name = "duration") val durata: Int,
    @ColumnInfo(name = "category") val categoria: String,
    @ColumnInfo(name = "toSee") val visibile: Boolean,
    @ColumnInfo(name = "image_uri") val imageUri: String? = null,
    @ColumnInfo(name = "visual") val visualizzazioni: Int,
    @ColumnInfo(name = "stars") var stelle: Int = 0

)

@Entity(tableName = "serie_tv_request")
data class SerieTV_Request(
    @PrimaryKey(autoGenerate = true) val id_request: Int = 0,
    @ColumnInfo(name = "serie_id") val serieId: Int,
    @ColumnInfo(name = "richiedente_id") val richiedenteId: Int,
    @ColumnInfo(name = "approvatore_id") val approvatoreId: Int,
    @ColumnInfo(name = "approvato") val approvato: Boolean
)

@Entity(
    tableName = "serie_tv_watched",
    primaryKeys = ["id_user", "serie_id"],
    foreignKeys = [
        ForeignKey(entity = Users::class, parentColumns = ["id_user"], childColumns = ["id_user"]),
        ForeignKey(entity = SerieTV::class, parentColumns = ["id_serie"], childColumns = ["serie_id"])
    ],
    indices = [Index("id_user"), Index("serie_id")]
)
data class SerieTV_Watched(
    @ColumnInfo(name = "id_user") val id_user: Int,
    @ColumnInfo(name = "serie_id") val serieId: Int,
)

@Entity(tableName = "trophy")
data class Trophy(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_trofeo") val id: Int = 0,
    @ColumnInfo(name = "nome") val nome: String,
)

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["id_user"],
            childColumns = ["id_user"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Trophy::class,
            parentColumns = ["id_trofeo"],
            childColumns = ["trofeoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_user"), Index("trofeoId")]
)
data class Achievements(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_achievement") val id: Int = 0,
    @ColumnInfo(name = "id_user") val id_user: Int,
    @ColumnInfo(name = "trofeoId") val trofeoId: Int,
    @ColumnInfo(name = "dataConseguimento") val dataConseguimento: Long = System.currentTimeMillis()
)

@Entity(tableName = "notify")
data class Notify(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_notifica") val id: Int = 0,
    @ColumnInfo(name = "contenuto") val contenuto: String,
    @ColumnInfo(name = "nome") val nome: String
)

@Entity(
    tableName = "reached_notify",
    primaryKeys = ["id_user", "notifica_id"],
    foreignKeys = [
        ForeignKey(entity = Users::class, parentColumns = ["id_user"], childColumns = ["id_user"]),
        ForeignKey(entity = Notify::class, parentColumns = ["id_notifica"], childColumns = ["notifica_id"])
    ],
    indices = [Index("id_user"), Index("notifica_id")]
)
data class Reached_Notify(
    @ColumnInfo(name = "id_user") val id_user: Int,
    @ColumnInfo(name = "notifica_id") val notificaId: Int,
    @ColumnInfo(name = "letta") val letta: Boolean
)

@Entity(
    tableName = "followers",
    primaryKeys = ["seguace_id", "seguito_id"],
    foreignKeys = [
        ForeignKey(entity = Users::class, parentColumns = ["id_user"], childColumns = ["seguace_id"]),
        ForeignKey(entity = Users::class, parentColumns = ["id_user"], childColumns = ["seguito_id"])
    ],
    indices = [Index("seguace_id"), Index("seguito_id")]
)
data class Followers(
    @ColumnInfo(name = "seguace_id") val seguaceId: Int,
    @ColumnInfo(name = "seguito_id") val seguitoId: Int
)

@Entity(tableName = "cinema")
data class Cinema(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cinema") val id: Int = 0,
    @ColumnInfo(name = "nome") val nome: String,
    @ColumnInfo(name = "indirizzo") val indirizzo: String,
    @ColumnInfo(name = "provincia") val provincia: String,

)

@Entity(
    tableName = "near_cinema",
    primaryKeys = ["id_user", "cinema_id"],
    foreignKeys = [
        ForeignKey(entity = Users::class, parentColumns = ["id_user"], childColumns = ["id_user"]),
        ForeignKey(entity = Cinema::class, parentColumns = ["id_cinema"], childColumns = ["cinema_id"])
    ],
    indices = [Index("id_user"), Index("cinema_id")]
)
data class CinemaVicini(
    @ColumnInfo(name = "id_user") val id_user: Int,
    @ColumnInfo(name = "cinema_id") val cinemaId: Int
)

@Entity(tableName = "actors")
data class Actors(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_attore") val id: Int = 0,
    @ColumnInfo(name = "nome") val nome: String,
    @ColumnInfo(name = "surname") val cognome: String
)

@Entity(
    tableName = "actors_in_film",
    primaryKeys = ["film_id", "attore_id"],
    foreignKeys = [
        ForeignKey(entity = Film::class, parentColumns = ["id_film"], childColumns = ["film_id"]),
        ForeignKey(entity = Actors::class, parentColumns = ["id_attore"], childColumns = ["attore_id"])
    ],
    indices = [Index("film_id"), Index("attore_id")]
)
data class Actors_In_Film(
    @ColumnInfo(name = "film_id") val filmId: Int,
    @ColumnInfo(name = "attore_id") val attoreId: Int,
)

@Entity(
    tableName = "actors_in_serie",
    primaryKeys = ["serie_id", "attore_id"],
    foreignKeys = [
        ForeignKey(entity = SerieTV::class, parentColumns = ["id_serie"], childColumns = ["serie_id"]),
        ForeignKey(entity = Actors::class, parentColumns = ["id_attore"], childColumns = ["attore_id"])
    ],
    indices = [Index("serie_id"), Index("attore_id")]
)
data class Actors_In_Serie(
    @ColumnInfo(name = "serie_id") val serieId: Int,
    @ColumnInfo(name = "attore_id") val attoreId: Int,
)

@Entity(tableName = "platform")
data class Platform(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_piattaforma") val id: Int = 0,
    @ColumnInfo(name = "nome") val nome: String
)

@Entity(
    tableName = "film_platform",
    primaryKeys = ["film_id", "piattaforma_id"],
    foreignKeys = [
        ForeignKey(entity = Film::class, parentColumns = ["id_film"], childColumns = ["film_id"]),
        ForeignKey(entity = Platform::class, parentColumns = ["id_piattaforma"], childColumns = ["piattaforma_id"])
    ],
    indices = [Index("film_id"), Index("piattaforma_id")]
)
data class Film_Platform(
    @ColumnInfo(name = "film_id") val filmId: Int,
    @ColumnInfo(name = "piattaforma_id") val piattaformaId: Int,
)

@Entity(
    tableName = "serie_platform",
    primaryKeys = ["serie_id", "piattaforma_id"],
    foreignKeys = [
        ForeignKey(entity = SerieTV::class, parentColumns = ["id_serie"], childColumns = ["serie_id"]),
        ForeignKey(entity = Platform::class, parentColumns = ["id_piattaforma"], childColumns = ["piattaforma_id"])
    ],
    indices = [Index("serie_id"), Index("piattaforma_id")]
)
data class Serie_Platform(
    @ColumnInfo(name = "serie_id") val serieId: Int,
    @ColumnInfo(name = "piattaforma_id") val piattaformaId: Int,
)

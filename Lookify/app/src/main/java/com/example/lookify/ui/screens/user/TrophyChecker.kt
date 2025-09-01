import com.example.lookify.ui.LookifyState
import com.example.lookify.data.database.Achievements

class TrophyChecker {

    fun checkTrophies(userId: Int, state: LookifyState): List<Int> {
        val newTrophies = mutableListOf<Int>()
        val userAchievements = state.achievements.filter { it.id_user == userId }
        val alreadyUnlocked = userAchievements.map { it.trofeoId }

        // Calcola statistiche utente
        val user = state.users.filter { it.id_user == userId }
        val watchedFilms = user.first().filmVisti
        val watchedSeries = state.watchedSeries.filter { it.id_user == userId }
        val totalWatched = watchedFilms.size + watchedSeries.size

        // Calcola tempo totale guardato
        val totalMinutes = calculateTotalWatchTime(userId, state)

        // Calcola generi diversi visti
        val uniqueGenres = getUniqueGenresWatched(userId, state)

        // Calcola followers
        val followersCount = state.followers.count { it.seguitoId == userId }

        // Verifica ogni trofeo
        state.trophies.forEach { trophy ->
            if (!alreadyUnlocked.contains(trophy.id)) {
                val shouldUnlock = when (trophy.nome) {
                    "Primo Film" -> totalWatched >= 1
                    "Cinefilo" -> watchedFilms.size >= 10
                    "Maratoneta" -> totalMinutes >= 600 // 10 ore
                    "Esploratore" -> uniqueGenres.size >= 5
                    "Fedele Spettatore" -> totalWatched >= 50
                    "Critico" -> hasRatedContent(userId, state)
                    "Nottambulo" -> hasWatchedAtNight(userId, state)
                    "Weekend Warrior" -> hasWatchedOnWeekend(userId, state)
                    "Collezionista" -> hasCompletedSeries(userId, state)
                    "Sociale" -> followersCount >= 10
                    else -> false
                }

                if (shouldUnlock) {
                    newTrophies.add(trophy.id)
                }
            }
        }

        return newTrophies
    }

    private fun calculateTotalWatchTime(userId: Int, state: LookifyState): Int {
        val filmsTime = state.watchedFilms
            .filter { it.utenteId == userId }
            .mapNotNull { watched ->
                state.films.find { it.id_film == watched.filmId }?.durata
            }.sum()

        val seriesTime = state.watchedSeries
            .filter { it.id_user == userId }
            .mapNotNull { watched ->
                state.series.find { it.id_serie == watched.serieId }?.durata
            }.sum()

        return filmsTime + seriesTime
    }

    private fun getUniqueGenresWatched(userId: Int, state: LookifyState): Set<String> {
        val filmGenres = state.watchedFilms
            .filter { it.utenteId == userId }
            .mapNotNull { watched ->
                state.films.find { it.id_film == watched.filmId }?.categoria
            }

        val seriesGenres = state.watchedSeries
            .filter { it.id_user == userId }
            .mapNotNull { watched ->
                state.series.find { it.id_serie == watched.serieId }?.categoria
            }

        return (filmGenres + seriesGenres).toSet()
    }

    private fun hasRatedContent(userId: Int, state: LookifyState): Boolean {
        // Assumendo che tu abbia una tabella ratings
        // return state.ratings.any { it.userId == userId }
        return false // Placeholder - implementa basandoti sulle tue tabelle
    }

    private fun hasWatchedAtNight(userId: Int, state: LookifyState): Boolean {
        // Logica per verificare orari di visione notturni
        // Necessita di timestamp nelle watchedFilms/watchedSeries
        return false // Placeholder
    }

    private fun hasWatchedOnWeekend(userId: Int, state: LookifyState): Boolean {
        // Logica per verificare visioni nel weekend
        return false // Placeholder
    }

    private fun hasCompletedSeries(userId: Int, state: LookifyState): Boolean {
        // Logica per verificare se ha completato almeno una serie
        val userSeries = state.watchedSeries.filter { it.id_user == userId }
        return userSeries.size >= 5 // Placeholder: ha visto almeno 5 episodi
    }
}
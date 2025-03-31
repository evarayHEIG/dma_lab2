package ch.heigvd.iict.dma.labo2.models

import org.altbeacon.beacon.Beacon
import java.util.*

class BeaconCache {
    private val beacons = HashMap<String, PersistentBeacon>()

    // Durée de validité d'une balise en millisecondes (5 secondes)
    companion object {
        const val EXPIRATION_MILLIS = 5000L
    }

    // Mettre à jour le cache avec les nouvelles balises détectées
    fun updateCache(newBeacons: List<PersistentBeacon>): List<PersistentBeacon> {
        val currentTime = System.currentTimeMillis()

        // Ajouter ou mettre à jour les balises détectées
        for (beacon in newBeacons) {
            beacon.lastSeen = currentTime
            val beaconId = beacon.id

            // Si la balise existe déjà, mettre à jour ses valeurs plutôt que de la remplacer
            if (beacons.containsKey(beaconId)) {
                beacons[beaconId]?.updateFrom(beacon)
            } else {
                beacons[beaconId] = beacon
            }
        }

        // Supprimer les balises expirées
        beacons.entries.removeIf {
            currentTime - it.value.lastSeen > EXPIRATION_MILLIS
        }

        // Retourner la liste complète des balises dans le cache
        return beacons.values.toList()
    }
}

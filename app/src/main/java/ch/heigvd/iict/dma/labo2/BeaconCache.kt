package ch.heigvd.iict.dma.labo2.models

import java.util.*

/**
 * Manages a cache of detected beacons, ensuring only active beacons are retained.
 * The cache automatically updates existing beacons and removes expired ones.
 *
 * @author Yanis Ouadahi
 * @author Rachel Tranchida
 * @author Eva Ray
 */
class BeaconCache {
    private val beacons = HashMap<String, PersistentBeacon>()

    /**
     * The validity duration of a beacon in milliseconds (10 seconds).
     */
    companion object {
        const val EXPIRATION_MILLIS = 10000L
    }

    /**
     * Updates the cache with newly detected beacons.
     * Beacons are either added or updated if they already exist in the cache.
     * Expired beacons are automatically removed.
     *
     * @param newBeacons The list of newly detected beacons.
     * @return The updated list of active beacons in the cache.
     */
    fun updateCache(newBeacons: List<PersistentBeacon>): List<PersistentBeacon> {
        val currentTime = System.currentTimeMillis()

        for (beacon in newBeacons) {
            beacon.lastSeen = currentTime
            val beaconId = beacon.id

            if (beacons.containsKey(beaconId)) {
                beacons[beaconId]?.updateFrom(beacon)
            } else {
                beacons[beaconId] = beacon
            }
        }

        // Remove expired beacons
        beacons.entries.removeIf {
            currentTime - it.value.lastSeen > EXPIRATION_MILLIS
        }

        return beacons.values.toList()
    }
}

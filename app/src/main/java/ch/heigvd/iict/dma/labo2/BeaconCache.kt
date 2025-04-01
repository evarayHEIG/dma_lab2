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

    /**
     * A map that stores beacons using their unique identifier as the key.
     * The value is a PersistentBeacon object representing the beacon's details.
     */
    private val beacons = HashMap<String, PersistentBeacon>()

    companion object {
        /**
         * The validity duration of a beacon in milliseconds (10 seconds).
         */
        const val EXPIRATION_MILLIS = 10000L

        /**
         * Known minors values for the beacons of our lab group
         */
        val GROUP_MINORS = intArrayOf(31, 38, 94)
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
            // Only keep beacons from our lab group
            // if (beacon.major !in GROUP_MINORS) continue
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

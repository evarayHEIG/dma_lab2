package ch.heigvd.iict.dma.labo2.models

import androidx.recyclerview.widget.DiffUtil
import org.altbeacon.beacon.Beacon
import java.util.*

/**
 * Represents a persistent beacon with details such as major, minor, UUID, signal strength, and last seen timestamp.
 * This class allows tracking beacons over time and provides methods for updating and converting beacon data.
 *
 * @author Yanis Ouadahi
 * @author Rachel Tranchida
 * @author Eva Ray
 */
data class PersistentBeacon(
    var major: Int,
    var minor: Int,
    var uuid: UUID,
    var rssi : Int,
    var txPower : Int,
    var distance : Double,
    var lastSeen: Long = System.currentTimeMillis() // Timestamp de dernière détection
) {

    companion object {

        /**
         * Converts a Beacon object into a PersistentBeacon.
         * @param beacon The beacon to be converted.
         * @return A PersistentBeacon instance containing the converted beacon data.
         */
        fun convertToPersistentBeacon(beacon: Beacon): PersistentBeacon {
            return PersistentBeacon(
                major = beacon.id2.toInt(),
                minor = beacon.id3.toInt(),
                uuid = UUID.fromString(beacon.id1.toString()),
                rssi = beacon.rssi,
                txPower = beacon.txPower,
                distance = beacon.distance,
                lastSeen = System.currentTimeMillis()
            )
        }
    }

    /**
     * Unique identifier for the beacon, composed of UUID, major, and minor values.
     */
    val id: String = "$uuid:$major:$minor"

    /**
     * Updates the values of this beacon with those from the provided beacon.
     * @param beacon The beacon from which to update the values.
     */
    fun updateFrom(beacon: PersistentBeacon) {
        rssi = beacon.rssi
        txPower = beacon.txPower
        distance = beacon.distance
        lastSeen = beacon.lastSeen
    }
}

class PersistentBeaconDiffCallback(private val oldList : List<PersistentBeacon>,
                                   private val newList : List<PersistentBeacon> ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return  old.major == new.major &&
                old.minor == new.minor &&
                old.uuid  == new.uuid &&
                old.rssi  == new.rssi &&
                old.txPower  == new.txPower &&
                old.distance == new.distance
    }

}

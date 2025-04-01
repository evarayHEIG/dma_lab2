package ch.heigvd.iict.dma.labo2.models

import androidx.recyclerview.widget.DiffUtil
import org.altbeacon.beacon.Beacon
import java.util.*

/*
 *  N'hésitez pas à ajouter des attributs ou des méthodes à cette classe
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

        // Convertir un objet Beacon en PersistentBeacon
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

    val id: String = "$uuid:$major:$minor"

    /**
     * Met à jour les valeurs de la balise avec celles de la balise passée en paramètre
     * @param beacon La balise à partir de laquelle mettre à jour les valeurs
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

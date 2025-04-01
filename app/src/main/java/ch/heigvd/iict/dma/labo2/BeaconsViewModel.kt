package ch.heigvd.iict.dma.labo2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ch.heigvd.iict.dma.labo2.models.BeaconCache
import ch.heigvd.iict.dma.labo2.models.PersistentBeacon
import org.altbeacon.beacon.Beacon

/**
 * ViewModel responsible for managing and providing beacon data to the UI.
 * It maintains a cache of detected beacons and ensures that the UI always receives an immutable and updated list.
 *
 * @author Yanis Ouadahi
 * @author Rachel Tranchida
 * @author Eva Ray
 */
class BeaconsViewModel : ViewModel() {

    private val _nearbyBeacons = MutableLiveData(mutableListOf<PersistentBeacon>())

    /**
     * Cache for managing beacon data.
     */
    private val beaconCache = BeaconCache()

    /*
     *  Remarque
     *  Il est important que le contenu de la LiveData nearbyBeacons, écoutée par l'interface
     *  graphique, soit immutable. Si on réalise "juste" un cast de la MutableLiveData vers la
     *  LiveData, par ex:
     *  val nearbyBeacons : LiveData<MutableList<PersistentBeacon>> = _nearbyBeacons
     *  L'interface graphique disposera d'une référence vers la même instance de liste encapsulée
     *  dans la MutableLiveData et la LiveData, contenant les références vers les mêmes
     *  instances de PersistentBeacon.
     *
     *  Ce qui implique que lorsque nous mettrons à jour les données de _nearbyBeacons après une
     *  annonce de la librairie, la liste référencée dans l'adapteur de la RecyclerView (qui est
     *  la même) sera également modifiée, créant ainsi une désynchronisation entre les données
     *  affichées à l'écran et les données présentent dans l'adapteur. Les deux listes étant
     *  strictement les mêmes, DiffUtil ne détectera aucun changement et l'interface graphique ne
     *  sera pas mise à jour.
     *  La solution présentée ici est de réaliser une projection d'une MutableList vers une List et
     *  une copie profonde de toutes les instances de PersistentBeacon qu'elle contient.
     */
    val nearbyBeacons : LiveData<List<PersistentBeacon>> = _nearbyBeacons.map { l -> l.toList().map { el -> el.copy() } }

    private val _closestBeacon = MutableLiveData<PersistentBeacon?>(null)
    val closestBeacon : LiveData<PersistentBeacon?> get() = _closestBeacon

    /**
     * Mapping of known minor values to location names.
     * Defaults to "Inconnu" if a major value is not listed.
     */
    val locations : Map<Int, String> = mapOf(
        31 to "Salon",
        38 to "Cuisine",
        94 to "Salle de bain",
    ).withDefault { "Inconnu" }

    /**
     * Updates the list of nearby beacons with newly detected ones.
     * Converts raw beacons into persistent format, updates the cache, and refreshes the LiveData values.
     *
     * @param beacons The list of newly detected beacons.
     */
    fun updateNearbyBeacons(beacons : List<Beacon>) {

        val newBeacons = beacons.map { PersistentBeacon.convertToPersistentBeacon(it) }

        val allBeacons = beaconCache.updateCache(newBeacons)

        _nearbyBeacons.value = allBeacons.toMutableList()

        _closestBeacon.value = allBeacons.minByOrNull { it.distance }
    }
}

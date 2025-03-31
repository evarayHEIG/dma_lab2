# DMA - LAB2

##### Authors: Yanis Ouadahi, Rachel Tranchida, Eva Ray

## 1. Lister les balises à portée

### Choix d'implémenation

### Questions

> 1.1.1 Est-ce que toutes les balises à proximité sont présentes dans toutes les annonces de la
> librairie ? Que faut-il mettre en place pour permettre de « lisser » les annonces et ne pas
> perdre momentanément certaines balises ?

Non, toutes les balises à proximité ne sont pas nécessairement présentes dans chaque annonce de la
librairie. Selon la documentation officielle de Android Beacon Library, les annonces Bluetooth sont
reçues de manière intermittente et peuvent être manquées en raison de plusieurs facteurs:

- __Nature intermittente des signaux BLE__ : Les balises transmettent typiquement leurs signaux à
  des
  intervalles de 100ms à 1s.
- __Scanning cyclique__ : Le smartphone ne scanne pas en continu mais par cycles, ce qui implique
  que
  certaines annonces peuvent être manquées.
- __Interférences et obstacles__ : Les obstacles physiques et interférences radio peuvent bloquer
  certaines transmissions.
- __Rotation des appareils__ : L'orientation du téléphone peut affecter la réception du signal.

Pour "lisser" les annonces et éviter de perdre momentanément certaines balises, il faut mettre en
place un mécanisme de persistance temporaire. Voici comment procéder:

1. Maintenir une liste de balises avec un timestamp de dernière détection
2. À chaque nouvelle annonce, mettre à jour les balises détectées
3. Conserver les balises non détectées pendant une période de grâce (par exemple 5-10 secondes)
4. Supprimer les balises qui n'ont pas été détectées pendant plus longtemps que cette période

Cette approche permet d'avoir une vue plus stable des balises à proximité, même si certaines ne
sont pas détectées à chaque cycle d'annonce.

> 1.1.2 Nous souhaitons effectuer un positionnement en arrière-plan, à quel moment faut-il démarrer
> et éteindre le monitoring des balises ? Sans le mettre en place, que faudrait-il faire pour
> pouvoir continuer le monitoring alors que l’activité n’est plus active ?

Aller voir la partie background detection
ici: [https://altbeacon.github.io/android-beacon-library/documentation.html](https://altbeacon.github.io/android-beacon-library/documentation.html)

> 1.1.3 On souhaite trier la liste des balises détectées de la plus proche à la plus éloignée,
> quelles sont
> les valeurs présentes dans les annonces reçues qui nous permettraient de le faire ? Comment
> sont-elles calculées et quelle est leur fiabilité ?
> Hint : N’hésitez pas à mettre en place un filtre pour limiter la détection uniquement aux iBeacons
> de
> votre groupe, le numéro mineur des balises est indiqué sur celles-ci.

En consultant
la [documentation officielle de la librairie concernant l'estimation des distance](https://altbeacon.github.io/android-beacon-library/distance-calculations.html),
nous constatons que l'API fournit une estimation directe de la distance en mètres, basée sur le
RSSI. Le RSSI est un indicateur du signal Bluetooth reçu : plus il est élevé (moins négatif),
plus la balise est proche. La valeur à utiliser pour le tri est donc la distance estimée.

D'après la documentation, la manière la plus précise de calculer la distance basée sur le RSSI est
obtenue en faisant une régression de puissance en utilisant une table connue de de valeurs distance/
RSSI pour un certain appareil. La fomrule utilisée est: `d=A*(r/t)^B+C` où

- `d` est la distance estimée en mètres
- `r` est le RSSI mesuré
- `t` est la puissance du signal à 1 mètre
- `A`, `B` et `C` sont des constantes spécifiques à l'appareil

Au niveau de la fiabilité, il ne faut pas attendre une trop grande précision pour la distance
estimée.
En effet, à 1 mètre, la variation peut être de 0.5 à 2 mètres, et au-delà, elle devient encore plus
grande (exemple : à 20 mètres, l’erreur peut aller de 10 à 40 mètres). Il y a plusieurs raisons à
cela :

- Les réflexions et obstacles (murs, objets, personnes).
- Les variations du signal sur le temps. La distance est estimée sur les 20 dernières secondes donc
  si l'appareil bouge, il faut attendre 20 secondes d'immobilité pour que la nouvele distance se
  stabilise (le filtrage appliqué par la bibliothèque tente de réduire cela).
- Les différences matérielles entre modèles de smartphones.

La stratégie de filtrage est donc la suivante :

- Récupérer la distance estimée fournie par l’API (beacon.getDistance()).
- Trier la liste des balises par ordre croissant de distance.
- Si besoin: appliquer un filtre pour n'afficher que les iBeacons du groupe (via le numéro mineur
  des balises).

## 2. Déterminer sa position

### Choix d'implémenation

### Questions

> 2.1.1 Comment pouvons-nous déterminer notre position ? Est-ce uniquement basé sur notion de
> proximité étudiée dans la question 1.1.3, selon vous est-ce que d’autres paramètres peuvent
> être pertinents ?

> 2.1.2 Les iBeacons sont conçus pour permettre du positionnement en intérieur. D’après l’expérience
> que vous avez acquise sur cette technologie dans ce laboratoire, quels sont les cas d’utilisation
> pour lesquels les iBeacons sont pleinement adaptés (minimum deux) ? Est-ce que vous voyez
> des limitations qui rendraient difficile leur utilisation pour certaines applications ?

Cas d'utilisation adaptés:

1. __Guidage et navigation en intérieur__ : Les iBeacons sont bien adaptés aux environnements où le
   GPS est inefficace, comme les centres commerciaux, les musées, ou les aéroports. Ils permettent
   aux utilisateurs de se repérer facilement grâce à une application qui détecte leur position
   approximative en fonction des balises les plus proches et leur propose un itinéraire.
2. __Proximité de points d'intérêt__ :Les commerces et espaces publics peuvent utiliser les iBeacons
   pour envoyer des notifications personnalisées aux utilisateurs lorsqu’ils passent à proximité
   d’un point d’intérêt. Par exemple, un magasin peut proposer une réduction sur un produit situé
   dans un rayon proche du client.

Limitations:

1. __Précision limitée__ : Les estimations de distance basées sur le RSSI (Received Signal Strength
   Indicator) sont sujettes à des variations dues aux obstacles, aux interférences et aux
   différences entre les modèles d’appareils. Cela limite leur précision pour un positionnement
   précis.
2. __Latence et temps de stabilisation__ : La technologie applique un lissage des mesures sur
   20 secondes, ce qui entraîne un retard dans la mise à jour de la position. Cela peut être
   problématique pour des applications nécessitant un suivi en temps réel.
3. __Dépendance au Bluetooth activé__ : L’utilisateur doit activer le Bluetooth sur son appareil, ce
   qui peut limiter l’adoption de cette technologie pour certaines applications.
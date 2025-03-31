# DMA - LAB2

##### Authors: Yanis Ouadahi, Rachel Tranchida, Eva Ray

## 1. Lister les balises à portée

### Choix d'implémenation

### Questions

> 1.1.1 Est-ce que toutes les balises à proximité sont présentes dans toutes les annonces de la
librairie ? Que faut-il mettre en place pour permettre de « lisser » les annonces et ne pas
perdre momentanément certaines balises ?

> 1.1.2 Nous souhaitons effectuer un positionnement en arrière-plan, à quel moment faut-il démarrer
et éteindre le monitoring des balises ? Sans le mettre en place, que faudrait-il faire pour
pouvoir continuer le monitoring alors que l’activité n’est plus active ?



> 1.1.3 On souhaite trier la liste des balises détectées de la plus proche à la plus éloignée, quelles sont
les valeurs présentes dans les annonces reçues qui nous permettraient de le faire ? Comment
sont-elles calculées et quelle est leur fiabilité ?
> Hint : N’hésitez pas à mettre en place un filtre pour limiter la détection uniquement aux iBeacons de
votre groupe, le numéro mineur des balises est indiqué sur celles-ci.

En consultant la [documentation officielle de la librairie concernant l'estimation des distance](https://altbeacon.github.io/android-beacon-library/distance-calculations.html),
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

Au niveau de la fiabilité, il ne faut pas attendre une trop grande précision pour la distance estimée.
En effet, à 1 mètre, la variation peut être de 0.5 à 2 mètres, et au-delà, elle devient encore plus 
grande (exemple : à 20 mètres, l’erreur peut aller de 10 à 40 mètres). Il y a plusieurs raisons à cela :
- Les réflexions et obstacles (murs, objets, personnes).
- Les variations du signal sur le temps. La distance est estimée sur les 20 dernières secondes donc
si l'appareil bouge, il faut attendre 20 secondes d'immobilité pour que la nouvele distance se
stabilise (le filtrage appliqué par la bibliothèque tente de réduire cela).
- Les différences matérielles entre modèles de smartphones.

La stratégie de filtrage est donc la suivante :
- Récupérer la distance estimée fournie par l’API (beacon.getDistance()).
- Trier la liste des balises par ordre croissant de distance.
- Si besoin: appliquer un filtre pour n'afficher que les iBeacons du groupe (via le numéro mineur des balises).

## 2. Déterminer sa position

### Choix d'implémenation

### Questions

> 2.1.1 Comment pouvons-nous déterminer notre position ? Est-ce uniquement basé sur notion de
proximité étudiée dans la question 1.1.3, selon vous est-ce que d’autres paramètres peuvent
être pertinents ?

> 2.1.2 Les iBeacons sont conçus pour permettre du positionnement en intérieur. D’après l’expérience
que vous avez acquise sur cette technologie dans ce laboratoire, quels sont les cas d’utilisation
pour lesquels les iBeacons sont pleinement adaptés (minimum deux) ? Est-ce que vous voyez
des limitations qui rendraient difficile leur utilisation pour certaines applications ?


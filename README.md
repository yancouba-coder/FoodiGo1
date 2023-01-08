# FoodiGo1
Bienvenu dans Foodigo. 

Foodigo est un jeu mobile developpé pour Android. 
Nous avons développé ce Jeu dans le cadre de notre formation de Master 2 MIAGE à l'université de Bordeaux.
Cejeu qui utilise les API : Google maps, Boussole et l'appareil photo et la réalité augmentée (ARCore).

Dès le démarrage de l'application il y'a un didactiel qui explique le fonctionnement du jeu.
Nous sommes partis sur le concept suivant:

Le joueur choisis son Tottem parmi 6 animaux. Le but du jeu est de le nourrir et de le voir grandir.
Le joueur doit donc chercher de la nourriture pour lui, ce sont les Foodies. Il s'agit d'une collection de fruits associés à un système de point. Le jeu fonctionne par palier, l'animal Tottem grandira et sera mis en jour dès que le palier de points est franchi. Chaque animal Tottem dispose de 3 versions pour les trois paliers du jeu. 

Les autorisations sont demandés dès la page d'accueil pour accéder à la position, à la caméra, au capteur de la boussole et au stockage.
Si ces autorisations sont accordées nous affichons la carte en zoomant sur la position de l'utilisateur
Les Foodies sont placés sur la Carte avec un rayon minimum et maximum par défaut qui sont de 1m et 3m respectivement.
L'utilisateur peut changer les rayons de recherche dans l'onglet Distance.

Dès que l'utlisateur se rapproche d'un Foodie, à la distance minimale déjà définie un Pop'up apparait et lui propose de prendre la photo du foodie  pour le capturer.
Dès que le Foodie est capturé, les points de l'utilisateur augmente et son Totem grandit. La photo est également enregistrée et sera accessible à la consultation depuuis la Galerie. 

*La classe LocationService nous donne la localisation dès que l'utilisateur change de position
*La classe CompassService nous donne l'orientation actuelle du téléphone, ceci nous permet notamment de savoir si le téléphone est dans la même direction que le Foodie.
*La classe MapsActivity gére toutes les fonctionnalité de la carte google Maps que nous avons intégrées.
*La classe MenuActivy affiche les différentes activités et fonctionnalité de l'application. Elle permet de naviguer depuis n'importe quelle classe.
*La classe AugmentedReality permet de positionner le Foodie sur le flux caméra et de prendre une photo en utiliisant la classe tempActivity.
*La Classe GalleryFoodies permet de voir tous les Foodies qui ont été capturé avec leurs photos.
*La Classe GalleryPhoto permet de consulter la photo d'un foodie déjà capturé. 
*La classe ManageFoodiesCaptured joue le rôle de gestionnaire de données. Nous utilisons les préférences pour stocker toutes les informations de paramétrage concernant la gestion des points, des préférences, des foodies capturés et d'autres informations liées au GamePlay.



Informations techniques : 
minSdk 27
targetSdk 32
emulé sur Pixel 4a 
Testé sur Xiaomi Mi11 Android 12

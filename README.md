# FoodiGo1
Nous avons développé ce Jeu dans le cadre de notre formation de Master 2 MIAGE à l'université de Bordeaux.
C'est un jeu qui consiste à utiliser les API : Google maps, Boussole et l'appareil photo et la réalité augmentée.

Dès le démarrage de l'application il y'a un didactiel qui explique le fonctionnement du jeu.
Nous sommes partis sur le concept suivant:

l'utilisateur choisis son Tottem.
Ensuite il va chercher de la nourriture pour lui, ces nourritures on les appeles Foodies.
Dès qu'il clique sur jouer nous demandons les autorisations pour accéder à la position et à la caméra.
Si ces autorisations sont accpordées nous affichons la carte en zoomant sur la position de l'utilisateur
Les Foodies sont placés sur la Carte avec un rayon minimum et maximum qui sont 2 m par défaut.
L'utilisateur peut changer les rayons de recherche dans settings.

Dès que l'utlisateur se rapproche à une distance minale déjà définis d'un Foodie. un Pop'up apparait et lui demande de prendre la photo du foodie.
Dès que le Foodie est capturé, les points de l'utilisateur augmente et son Totem grandit.

*La classe LocationService nous donne la localisation dès que l'utilisateur change de position
*La classe CompassService nous donne l'orientation actuelle du téléphone, ceci nous permet notamment de savoir si le téléphone est dans la même direction que le Foodie.
*La classe MapsActivity gére toutes les fonctionnalité de la carte google Maps que nous avons intégré.
*La classe MenuActivy affiche les différentes activités et fonctionnalité de l'application. Elle permet de naviguer depuis n'importe quelle classe
*La classe PhotoActivity permet de prendre une photo en se basant sur l'orientation du téléphone
*La classe AugmentedReality permet de positionner le Foodie sur le flux caméra et de prendre une photo.
*La Classe Gallery permet de voir tous les Foodies qui ont été capturé avec leurs photos.
*La classe ManageFoodiesCaptured joue le rôle de gestionnaire de données. nous utilisons les préférences pour stocker toutes les informations de paramétrage à savoir ,les Foodies, les distances 
et le Totem choisi.



package com.example.foodigo1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ManageFoodiesCaptured {

        private static ManageFoodiesCaptured instance;
        private static Context contextApp;
        private ManageFoodiesCaptured() {
            // private constructor to prevent external instantiation
        }

        /**
        Cette classe est uun singleton istancée par getInstnce() par le reste de l'application
         */
        public static ManageFoodiesCaptured getInstance(Context context) {
            if (instance == null) {
                instance = new ManageFoodiesCaptured();
                contextApp = context;
            }
            return instance;
        }



        /* *************************************************************************************************/
        /* ****************************************** GamePlay *********************************************/

        /**
        Retourne un booléan indiquant si le foodie a déjà été capturé
         */
        public Boolean isCaptured (String name){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile), Context.MODE_PRIVATE);
            Boolean result = sharedPref.getBoolean(name, false);
            return result;
        }

        /**
        Permet d'enregistrer la capture d'un foodie
        A utiliser lorsque le foodie a bien été capturé et qu'une photo a été prise -> absolutePath = chemin de la photo dans le stockage
         */
        public void newFoodieCaptured (String foodieName, String absolutePath){
            writeToPreferences(foodieName, true);
            addVictoryToPoints(getPointOfFoodie(foodieName));
            addAbsolutePathToPreference(foodieName, absolutePath);
            Log.e("manager","newFoodieCaptured : " + absolutePath);

        }


        /**
        Permet de savoir si tout les foodies ont été capturés, renvoie false si il reste au moins un foodie à capturer.
         */
        public boolean gameIsComplete () {
            for (String foodie : getListOfFoodies()) {
                if (!isCaptured(foodie)) {
                    return false;
                }
            }
            return true;
        }



        /* *************************************************************************************************/
        /* ***************************************** Les listes ********************************************/

        /**
        Cette méthode permet d'afficher la liste des photos dans la console
         */
        private void listPhotos () {
            // Obtenir le répertoire où sont enregistrées les photos
            //retourne un objet File qui représente le répertoire où sont enregistrés les fichiers externes de l'application.
            File photoDir = contextApp.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            // Vérifier que le répertoire existe
            if (photoDir != null) {
                // Obtenir la liste des fichiers dans le répertoire
                File[] photoFiles = photoDir.listFiles();


                // Vérifier que la liste n'est pas vide
                if (photoFiles != null) {
                    // Parcourir la liste des fichiers et afficher leur nom
                    for (File file : photoFiles) {
                        if (file.isFile()) {
                            System.out.println(file.getName());
                        }
                    }
                }
            }
        }

        /**
        Retourne la liste des foodies sous forme de strings
        */
        public List<String> getListOfFoodies () {
                return Arrays.asList("ananas", "avocat", "banane", "pasteque", "mangue", "pommes");
            }



        /* *************************************************************************************************/
        /* **************************************** Préférences ********************************************/
        /**
        Lorsque la photo du foodie est prise, le chemin de la photo est sauvegardé en tant que string dans les preferences
         */
        private void addAbsolutePathToPreference (String foodieName, String absolutePath){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfAbsolutePathPreference), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(foodieName, absolutePath);
            editor.apply();
            showThePrefrerencesInConsole("addAbsolutePathToPreference : ", R.string.nameOfAbsolutePathPreference);
        }


        /**
        Retourne le chemin du fichier de l'image enregistré sur le téléphone. Retourne null si le foodie n'a pas été capturé et leve
        une exception si le foodie a été capturé mais que le chemin de la photo n'a pas été enregistré.
         */
        public String getAbsolutePathFromPreference (String foodieName) throws Exception {
        SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfAbsolutePathPreference), Context.MODE_PRIVATE);
        String result = null;
        if (isCaptured(foodieName)) { //le foodie a été capturé donc il a logiquement une photo
            result = sharedPref.getString(foodieName, "");
        } else { // le foodie n'a pas encore été capturé
            result = null;
        }
        if (result == "") {
            throw new Exception("le foodie n'a pas de photo");
        } //le foodie a été capturé mais il n'a pas de photo
        return result;
    }

        /**
        Permet d'enregistrer la capture d'un foodie dans les préférences
         */
        public void writeToPreferences (String name, Boolean valueToSave){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(name, valueToSave);
            editor.apply();
            showThePrefrerencesInConsole("writeToPreferences : ", R.string.nameOfPreferencesFile);
        }

        /**
        Stocker la distance Maximale et minimale de recherche
        */
        public void writeToPreferencesDistance (int maxDistance, int minDistance ){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesDistances), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("MinimumDistance", minDistance);
            editor.putInt("MaximumDistance", maxDistance);
            editor.apply();
            showThePrefrerencesInConsole("writeToPreferences : ", R.string.nameOfPreferencesFile);
        }

        /**
        Cette méthode retourne la distance min ou max de la zone de jeu
         */
        public int getDistance (String name){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesDistances), Context.MODE_PRIVATE);
            int result = sharedPref.getInt(name, 2);
            return result;
        }

        /**
        Supprime toutes les préférences
         */
        private void clearAllPreference ( int idPref){
            //Supprimer toutes les valeurs
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(idPref), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            showThePrefrerencesInConsole("clearAllPreference : ", R.string.nameOfPreferencesFile);
        }

        /**
        Retire une préference de la liste "capturedFoodie"
         */
        public void removeAPreference (String name){
            //Supprime une seule valeur
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(name);
            editor.apply();
            showThePrefrerencesInConsole("removeAPreference : ", R.string.nameOfPreferencesFile);
        }

        /**
        Initialise "captureFoodie" avec les valers à false, soir les foodies n'ont pas encore été capturé.
         */
        public void initPreferences () {
            //préférences des foodies captured
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile), Context.MODE_PRIVATE);
            if (!sharedPref.contains("ananas")) {
                writeToPreferences("ananas", false);
            }
            if (!sharedPref.contains("avocat")) {
                writeToPreferences("avocat", false);
            }
            if (!sharedPref.contains("banane")) {
                writeToPreferences("banane", false);
            }
            if (!sharedPref.contains("pasteque")) {
                writeToPreferences("pasteque", false);
            }
            if (!sharedPref.contains("mangue")) {
                writeToPreferences("mangue", false);
            }
            if (!sharedPref.contains("pommes")) {
                writeToPreferences("pommes", false);
            }
            showThePrefrerencesInConsole("initPreferences", R.string.nameOfPreferencesFile);

            //preference des points
            sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPointsSysteme), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("points", 0);
            showThePrefrerencesInConsole("initPreferences", R.string.nameOfPointsSysteme);

            //préférences des Distances
            writeToPreferencesDistance (3,1 );
            showThePrefrerencesInConsole("initPreferences", R.string.nameOfPreferencesDistances);
            //pas besoin d'initialiser les préférences des absolutePath des photos
        }

        /**
        Permet de réinitialiser les préférences du jeu à l'état initial
         */
        public void reInitPreferences () {
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile), Context.MODE_PRIVATE);
            clearAllPreference(R.string.nameOfPreferencesFile);
            clearAllPreference(R.string.nameOfAbsolutePathPreference);
            clearAllPreference(R.string.nameOfPointsSysteme);
            clearAllPreference(R.string.nameOfPreferencesDistances);
            clearAllPreference(R.string.nameOfDidacticiel);

            initPreferences();
            showThePrefrerencesInConsole("reInitPreferences", R.string.nameOfPreferencesFile);

        }

        /**
        Affiche la lise des variables enregistrées dans les préférences dans la console
        */
        public void showThePrefrerencesInConsole (String strBefore,int nameOfPreferencce){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(nameOfPreferencce), Context.MODE_PRIVATE);
            System.out.println(strBefore + sharedPref.getAll().toString());
        }



        /* *************************************************************************************************/
        /* ***************************************** Drawables *********************************************/

        /**
        Cette fonction retourne l'image en couleur du foodie qui lui ai passé en paramètre.
         */
        public int getIdOfDrawableFoodie(String foodieName){
            switch (foodieName){
                case "ananas":
                    return R.drawable.ananas;
                case "avocat":
                    return R.drawable.avocat;
                case "banane":
                    return R.drawable.banane;
                case "pasteque":
                    return R.drawable.pasteque;
                case "mangue":
                    return R.drawable.mangue;
                case "pommes":
                    return R.drawable.pommes;
                default:
                    Log.e("manager.getDrawableFoodie() : ","getDrawableFoodie() appelé mais foodieName ne correspond à aucune valeur connue. foodieName : " + foodieName);
                    return 0 ;

            }

        }

        /**
        Run the display methods to change color
         */
        public void displayCapturedFoodie(GalleryFoodiesActivity gallery){
            displayCheckBox(gallery,"ananas", R.id.ananasCheck);
            displayCheckBox(gallery,"avocat",R.id.avocatCheck);
            displayCheckBox(gallery,"banane",R.id.bananeCheck);
            displayCheckBox(gallery,"pasteque",R.id.pastequeCheck);
            displayCheckBox(gallery,"mangue",R.id.mangueCheck);
            displayCheckBox(gallery,"pommes",R.id.pommesCheck);

            displayImageFoodie(gallery,"ananas",R.id.ananasImage,R.drawable.ananas,R.drawable.ananas_black);
            displayImageFoodie(gallery,"avocat",R.id.avocatImage,R.drawable.avocat,R.drawable.avocat_black);
            displayImageFoodie(gallery,"banane",R.id.bananeImage,R.drawable.banane,R.drawable.banane_black);
            displayImageFoodie(gallery,"pasteque",R.id.pastequeImage,R.drawable.pasteque,R.drawable.pasteque_black);
            displayImageFoodie(gallery,"mangue",R.id.mangueImage,R.drawable.mangue,R.drawable.mangue_black);
            displayImageFoodie(gallery,"pommes",R.id.pommesImage,R.drawable.pommes,R.drawable.pommes_black);

            displayPicture(gallery,"ananas",R.id.ananasPicture,R.drawable.img_ico_green,R.drawable.img_ico_black);
            displayPicture(gallery,"avocat",R.id.avocatPicture,R.drawable.img_ico_green,R.drawable.img_ico_black);
            displayPicture(gallery,"banane",R.id.bananePicture,R.drawable.img_ico_green,R.drawable.img_ico_black);
            displayPicture(gallery,"pasteque",R.id.pastequePicture,R.drawable.img_ico_green,R.drawable.img_ico_black);
            displayPicture(gallery,"mangue",R.id.manguePicture,R.drawable.img_ico_green,R.drawable.img_ico_black);
            displayPicture(gallery,"pommes",R.id.pommesPicture,R.drawable.img_ico_green,R.drawable.img_ico_black);

        }

        /**
        Affiche le foodie en couleur si il a été capturé sinon en gris dans MapsActivity
         */
        public void displayCapturedFoodieForMapsActivity(Maps3Activity activity){
            displayImageFoodie(activity,"ananas",R.id.ananasImage,R.drawable.ananas,R.drawable.ananas_black);
            displayImageFoodie(activity,"avocat",R.id.avocatImage,R.drawable.avocat,R.drawable.avocat_black);
            displayImageFoodie(activity,"banane",R.id.bananeImage,R.drawable.banane,R.drawable.banane_black);
            displayImageFoodie(activity,"pasteque",R.id.pastequeImage,R.drawable.pasteque,R.drawable.pasteque_black);
            displayImageFoodie(activity,"mangue",R.id.mangueImage,R.drawable.mangue,R.drawable.mangue_black);
            displayImageFoodie(activity,"pommes",R.id.pommesImage,R.drawable.pommes,R.drawable.pommes_black);

        }

        /**
        Affiche l'icone de la photo en couleur si le foodie a été capturé sinon en nuance de gris
         */
        private void displayPicture(Activity activity, String foodieName, int idImageView, int imageColor, int imageBlack){
            try {
                ImageView iw = activity.findViewById(idImageView);
                if (isCaptured(foodieName)){iw.setImageResource(imageColor); } else {iw.setImageResource(imageBlack);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
        Affiche l'image en couleur si il a été capturé sinon en nuance de gris
         */
        private void displayImageFoodie(Activity activity,String foodieName, int idImageView, int imageColor, int imageBlack)
        {
            try {
                ImageView iw = activity.findViewById(idImageView);
                if (isCaptured(foodieName)){iw.setImageResource(imageColor); } else {iw.setImageResource(imageBlack);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
        AAffiche la checkBox du foodie si il a été capturé
         */
        private void displayCheckBox(Activity activity,String foodieName, int idCheck) {
            try {
                ImageView iw = activity.findViewById(idCheck);
                if (isCaptured(foodieName)){ iw.setVisibility(View.VISIBLE); } else {iw.setVisibility(View.INVISIBLE);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
        Cette fonction retourne l'image en couleur du foodie qui lui ai passé en paramètre.
         */
        public Drawable getDrawableFoodie(String foodieName) {
        switch (foodieName) {
            case "ananas":
                return contextApp.getDrawable(R.drawable.ananas);
            case "avocat":
                return contextApp.getDrawable(R.drawable.avocat);
            case "banane":
                return contextApp.getDrawable(R.drawable.banane);
            case "pasteque":
                return contextApp.getDrawable(R.drawable.pasteque);
            case "mangue":
                return contextApp.getDrawable(R.drawable.mangue);
            case "pommes":
                return contextApp.getDrawable(R.drawable.pommes);
            default:
                Log.e("manager.getDrawableFoodie() : ", "getDrawableFoodie() appelé mais foodieName ne correspond à aucune valeur connue. foodieName : " + foodieName);
                return null;
        }
    }

        /* *************************************************************************************************/
        /* ******************************************* Points **********************************************/

        /**
        Modifie les points dans les preferences du jeu en remplacant par la nouvelle valeur
         */
        private void updatePointsToPreference ( int points){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPointsSysteme), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int newPoints = points; // la diff par rapport a addVictoryToPoints() est ici.
            editor.putInt("points", newPoints);
            editor.apply();
            showThePrefrerencesInConsole("writeToPreferences : ", R.string.nameOfPointsSysteme);
        }


        /**
        Modifie les points dans les preferences du jeu lors d'une victoire
         */
        private void addVictoryToPoints ( int points){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPointsSysteme), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int newPoints = sharedPref.getInt("points", 0) + points;
            editor.putInt("points", newPoints);
            editor.apply();
            showThePrefrerencesInConsole("writeToPreferences : ", R.string.nameOfPointsSysteme);
        }


        /**
        Retourne le nombre de points que rapporte un foodie une fois capturés
         */
        public int getPointOfFoodie (String foodieName){
            switch (foodieName) {
                case "ananas":
                    return contextApp.getResources().getInteger(R.integer.ananasXP);
                case "avocat":
                    return contextApp.getResources().getInteger(R.integer.avocatXP);
                case "banane":
                    return contextApp.getResources().getInteger(R.integer.bananeXP);
                case "pasteque":
                    return contextApp.getResources().getInteger(R.integer.pastequeXP);
                case "mangue":
                    return contextApp.getResources().getInteger(R.integer.mangueXP);
                case "pommes":
                    return contextApp.getResources().getInteger(R.integer.pommesXP);
                default:
                    System.out.println("getPointOfFoodie() appelé mais foodieName ne correspond à aucune valeur connue. foodieName : " + foodieName);
                    return 0;

            }
        }

        /**
        Met à jour l'affichage du score dans les vues
        */
        public void updatePoints (Activity activity){
            TextView pointsView = activity.findViewById(R.id.points);
            pointsView.setText(getPoints() + " XP");
        }

        /**
        Retourne le score en cours du joueur et s'assure qu'il est correct, sinon il est mise à jour
         */
        public int getPoints () {
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPointsSysteme), Context.MODE_PRIVATE);
            int pointsByRef = sharedPref.getInt("points", 0);
            List<String> foodieNames = Arrays.asList("ananas", "avocat", "banane", "pasteque", "mangue", "pommes");
            int pointsByCalcul = 0;
            for (String foodie : foodieNames) {
                if (isCaptured(foodie)) {
                    pointsByCalcul += this.getPointOfFoodie(foodie);
                    //System.out.println("points des foodies : " + foodie + " a points : " + this.getPointOfFoodie(foodie));
                }
            }
            if (pointsByCalcul != pointsByRef) {
                System.out.println("ManageFoodiesCaptured.getPoints() : les points par référence : " + pointsByRef + " ne sont pas égaux aux points par calculs : " + pointsByCalcul);
                System.out.println("les points par reférence sont mise à jour avec les points par calculs");
                this.updatePointsToPreference(pointsByCalcul);
            }

            return pointsByCalcul;
        }

        /**************************************************************************************************/
        /**************************************** Les tottems *********************************************/

        /**
        Stocker le choix du totem
        */
        public void writeToPreferenceTotem(String totemName){

            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPrefencesTotem), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("Totemname", totemName);
            editor.apply();
          }

        public String getTotem(){

            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPrefencesTotem), Context.MODE_PRIVATE);

            String result= sharedPref.getString("Totemname","Licorne");

            return result;

        }



        public Drawable getDrawaleByPoints(String totemName, int points){

            if(points<200)

                return getDrawableTotem1(totemName);

            else if(points>200 && points<600)

                return getDrawableTotem2(totemName);

            else

               return  getDrawableTotem3(totemName);

        }







        public Drawable getDrawableTotem1(String totemName) {

            switch (totemName) {

                case "Buldog":

                    return contextApp.getDrawable(R.drawable.bulldog_1);

                case "Elephant":

                    return contextApp.getDrawable(R.drawable.elefant_1);

                case "Giraffe":

                    return contextApp.getDrawable(R.drawable.giraffe_1);

                case "Cheval":

                    return contextApp.getDrawable(R.drawable.horse_1);

                case "Cochon":

                    return contextApp.getDrawable(R.drawable.pig_1);

                case "Licorne":

                    return contextApp.getDrawable(R.drawable.unicorn_1);

                default:

                    Log.e("manager.getDrawableFoodie() : ", "getDrawableFoodie() appelé mais totem ne correspond à aucune valeur connue. : " + totemName);

                    return null;

            }

        }

        public Drawable getDrawableTotem2(String totemName) {

            switch (totemName) {

                case "Buldog":

                    return contextApp.getDrawable(R.drawable.bulldog_2);

                case "Elephant":

                    return contextApp.getDrawable(R.drawable.elefant_2);

                case "Giraffe":

                    return contextApp.getDrawable(R.drawable.giraffe_2);

                case "Cheval":

                    return contextApp.getDrawable(R.drawable.horse_2);

                case "Cochon":

                    return contextApp.getDrawable(R.drawable.pig_2);

                case "Licorne":

                    return contextApp.getDrawable(R.drawable.unicorn_2);

                default:

                    Log.e("manager.getDrawableFoodie() : ", "getDrawableFoodie() appelé mais totem ne correspond à aucune valeur connue.  : " + totemName);

                    return null;

            }

        }

        public Drawable getDrawableTotem3(String totemName) {

            switch (totemName) {

                case "Buldog":

                    return contextApp.getDrawable(R.drawable.bulldog_3);

                case "Elephant":

                    return contextApp.getDrawable(R.drawable.elefant_3);

                case "Giraffe":

                    return contextApp.getDrawable(R.drawable.giraffe_3);

                case "Cheval":

                    return contextApp.getDrawable(R.drawable.horse_3);

                case "Cochon":

                    return contextApp.getDrawable(R.drawable.pig_3);

                case "Licorne":

                    return contextApp.getDrawable(R.drawable.unicorn_3);

                default:

                    Log.e("manager.getDrawableFoodie() : ", "getDrawableFoodie() appelé mais totem ne correspond à aucune valeur connue.  : " + totemName);

                    return null;

            }
        }

        public String getTotemNameWithTotemid(int buttonRadId){

            String result="";

            switch (buttonRadId){

                case R.id.buldogImage:

                    result="Buldog";
                    break;

                case R.id.elephantImage:

                    result= "Elephant";
                    break;

                case R.id.Giraffe:

                    result= "Giraffe";
                    break;

                case R.id.horseImage:

                    result= "Cheval";
                    break;

                case R.id.cochonImage:

                    result= "Cochon";
                    break;

                case R.id.LicorneImage:

                    result= "Licorne";
                    break;
                default:
                    break;

            }

            return result;




        }

        public Drawable getTotemDrawable(int id){

            switch (id){

                case R.id.buldogImage:

                    return contextApp.getDrawable(R.drawable.bulldog_1);

                case R.id.elephantImage:


                    return contextApp.getDrawable(R.drawable.elefant_1);

                case R.id.Giraffe:

                    return contextApp.getDrawable(R.drawable.giraffe_1);

                case R.id.horseImage:

                    return contextApp.getDrawable(R.drawable.horse_1);

                case R.id.cochonImage:

                    return contextApp.getDrawable(R.drawable.pig_1);

                case R.id.LicorneImage:

                    return contextApp.getDrawable(R.drawable.unicorn_1);

                default:

                    return contextApp.getDrawable(R.drawable.unicorn_1);




            }







        }


        public int getIDDrawableTotem() {
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPrefencesTotem), Context.MODE_PRIVATE);

            String totemName= sharedPref.getString("Totemname","Licorne");

            switch (totemName) {

            case "Buldog":

                return R.drawable.bulldog_1;

            case "Elephant":

                return R.drawable.elefant_1;

            case "Giraffe":

                return R.drawable.giraffe_1;

            case "Cheval":

                return R.drawable.horse_1;

            case "Cochon":

                return R.drawable.pig_1;

            case "Licorne":

                return R.drawable.unicorn_1;

            default:

                Log.e("manager.getDrawableFoodie() : ", "getDrawableFoodie() appelé mais totem ne correspond à aucune valeur connue. : " + totemName);

                return 0;

        }

    }




    /* *************************************************************************************************/
        /* ************************ Ancienne méthodes liées aux fichiers JSON ******************************/

        /*
        Renvoi un boolean indiquant si le foodie a déjà été capturé selon l'information stocké dans le fichier JSON
        N'est plus utilisé, on a opté pour les préférences.
         */
        /*
        private Boolean OLDisCaptured (String foodie) throws Exception {
                //OLD method, actually not used
                JSONObject capturedJSON = readJson("captured.json");
                HashMap<String, Boolean> captured = new Gson().fromJson(String.valueOf(capturedJSON), HashMap.class);

                if (captured.containsKey(foodie)) {
                    return captured.get(foodie);
                } else {
                    throw new Exception("The foodie " + foodie + " doesn't exist");
                }

            }

         */






        /* *************************************************************************************************/
        /* ************************ Ancienne méthodes liées aux fichiers JSON ******************************/

        /*
        Renvoi un boolean indiquant si le foodie a déjà été capturé selon l'information stocké dans le fichier JSON
        N'est plus utilisé, on a opté pour les préférences.
         */
        /*
        private Boolean OLDisCaptured (String foodie) throws Exception {
                //OLD method, actually not used
                JSONObject capturedJSON = readJson("captured.json");
                HashMap<String, Boolean> captured = new Gson().fromJson(String.valueOf(capturedJSON), HashMap.class);

                if (captured.containsKey(foodie)) {
                    return captured.get(foodie);
                } else {
                    throw new Exception("The foodie " + foodie + " doesn't exist");
                }
            }

         */


        /*
        Permet de lire les informations contenues dans un fichier JSON,  non utilisé on a opté pour les préférences
         */
        /*
        public JSONObject readJson (String filename){

            AssetManager assetManager = contextApp.getAssets();
            String jsonString = "";
            // Read the JSON file and store it as a string

            try {
                InputStream inputStream = assetManager.open(filename);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                jsonString = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Parse the JSON string and do something with the data
            try {
                JSONObject json = new JSONObject(jsonString);
                //System.out.println(json.toString());
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

         */

        /*
        Problème d'utilisation des fichiers JSON, on opté pour les préférences.
         */
        /*
        public void UdapteFoodInJson( String foodname, boolean isCaptured, File filename) throws IOException {
                    //On ouvre le fichier captured.json en mode écriture
                    //On récupére tout le contenu du fichier JSON dans un HashMap
            JSONObject capturedJSON = readJson("captured.json");
            HashMap<String, Boolean> foodies = new Gson().fromJson(String.valueOf(capturedJSON), HashMap.class);
            Boolean replace = foodies.replace(foodname, isCaptured);

            if(replace!=null){
                System.out.println( foodname+" a été remplacé dans le Hashmap");
                //On convertit le Hashmap en JSON object
                Gson gson = new Gson();
                String json = gson.toJson(foodies);
                System.out.println(json);
                //Puis on reécrit tout le hashmap dans le JSON

                //WriteToFile(contextApp, "captured.json", json);

            }
            else{
                System.out.println(foodname + "n'a pas pu être remplacé");
            }
            }*/


}



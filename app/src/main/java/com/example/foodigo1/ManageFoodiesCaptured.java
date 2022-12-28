package com.example.foodigo1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ManageFoodiesCaptured {

        private static ManageFoodiesCaptured instance;
        private static Context contextApp;
        private ManageFoodiesCaptured() {
            // private constructor to prevent external instantiation
        }

        public static ManageFoodiesCaptured getInstance(Context context) {
            if (instance == null) {
                instance = new ManageFoodiesCaptured();
                contextApp = context;
            }
            return instance;
        }
        //Run the display methods to change color
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

        public void displayCapturedFoodieForMapsActivity(Maps3Activity activity){
            displayImageFoodie(activity,"ananas",R.id.ananasImage,R.drawable.ananas,R.drawable.ananas_black);
            displayImageFoodie(activity,"avocat",R.id.avocatImage,R.drawable.avocat,R.drawable.avocat_black);
            displayImageFoodie(activity,"banane",R.id.bananeImage,R.drawable.banane,R.drawable.banane_black);
            displayImageFoodie(activity,"pasteque",R.id.pastequeImage,R.drawable.pasteque,R.drawable.pasteque_black);
            displayImageFoodie(activity,"mangue",R.id.mangueImage,R.drawable.mangue,R.drawable.mangue_black);
            displayImageFoodie(activity,"pommes",R.id.pommesImage,R.drawable.pommes,R.drawable.pommes_black);

        }

        //Affiche l'icone de la photo en couleur si le foodie a été capturé sinon en nuance de gris
        private void displayPicture(Activity activity, String nameInJson, int idImageView, int imageColor, int imageBlack){
            try {
                ImageView iw = activity.findViewById(idImageView);
                if (isCaptured(nameInJson)){iw.setImageResource(imageColor); } else {iw.setImageResource(imageBlack);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Affiche l'image en couleur si il a été capturé sinon en nuance de gris
        private void displayImageFoodie(Activity activity,String nameInJson, int idImageView, int imageColor, int imageBlack){
            try {
                ImageView iw = activity.findViewById(idImageView);
                if (isCaptured(nameInJson)){iw.setImageResource(imageColor); } else {iw.setImageResource(imageBlack);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //affiche la checkBox du foodie si il a été capturé
        private void displayCheckBox(Activity activity,String nameInJson, int idCheck) {
            try {
                ImageView iw = activity.findViewById(idCheck);
                if (isCaptured(nameInJson)){ iw.setVisibility(View.VISIBLE); } else {iw.setVisibility(View.INVISIBLE);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void displayPhotoFoodieCaptured(GalleryPhotoActivity photoActivity, String nameInJson, int idImageView){
            ImageView iv = photoActivity.findViewById(idImageView);
            int idDeLImageDansLeStockage = 1;
            iv.setImageResource(idDeLImageDansLeStockage);
        }
        //Renvoi un boolean indiquant si le foodie a déjà été capturé selon l'information stocké dans le fichier JSON
        private Boolean OLDisCaptured(String foodie) throws Exception {
            //OLD method, actually not used
            JSONObject capturedJSON = readJson("captured.json");
            HashMap<String, Boolean> captured = new Gson().fromJson(String.valueOf(capturedJSON), HashMap.class);

            if (captured.containsKey(foodie)){
                return captured.get(foodie);
            }else{
                throw new Exception("The foodie " + foodie + " doesn't exist");
            }
        }

        public JSONObject readJson(String filename){

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
        // other methods and fields go here

        public void writeToPreferences(String name, Boolean valueToSave){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(name, valueToSave);
            editor.apply();
            showThePrefrerencesInConsole("writeToPreferences : ");
        }
        private void clearAllPreference(){
            //Supprimer toutes les valeurs
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            showThePrefrerencesInConsole("clearAllPreference : ");
        }
        public void removeAPreference(String name){
            //Supprime une seule valeur
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(name);
            editor.apply();
            showThePrefrerencesInConsole("removeAPreference : ");
        }

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

        WriteToFile(contextApp, "captured.json", json);

    }
    else{
        System.out.println(foodname + "n'a pas pu être remplacé");
    }
    }





        public void showThePrefrerencesInConsole(String strBefore){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            System.out.println(strBefore + sharedPref.getAll().toString());
        }


        public Boolean isCaptured(String name){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            Boolean result = sharedPref.getBoolean(name,false);
            return result;
        }



        public void initPreferences(){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            if (!sharedPref.contains("ananas")){writeToPreferences("ananas",false);}
            if (!sharedPref.contains("avocat")){writeToPreferences("avocat",false);}
            if (!sharedPref.contains("banane")){writeToPreferences("banane",false);}
            if (!sharedPref.contains("pasteque")){writeToPreferences("pasteque",false);}
            if (!sharedPref.contains("mangue")){writeToPreferences("mangue",false);}
            if (!sharedPref.contains("pommes")){writeToPreferences("pommes",false);}
            showThePrefrerencesInConsole("initPreferences");
        }


        public void reInitPreferences(){
            SharedPreferences sharedPref = contextApp.getSharedPreferences(String.valueOf(R.string.nameOfPreferencesFile),Context.MODE_PRIVATE);
            clearAllPreference();
            removeAPreference("test");
            initPreferences();
            showThePrefrerencesInConsole("reInitPreferences");

        }




}

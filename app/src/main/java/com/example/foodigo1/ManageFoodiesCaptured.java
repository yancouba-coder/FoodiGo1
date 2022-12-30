package com.example.foodigo1;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.ImageView;

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

        //Affiche l'icone de la photo en couleur si le foodie a été capturé sinon en nuance de gris
        private void displayPicture(GalleryFoodiesActivity gallery, String nameInJson, int idImageView, int imageColor, int imageBlack){
            try {
                ImageView iw = gallery.findViewById(idImageView);
                if (isCaptured(nameInJson)){iw.setImageResource(imageColor); } else {iw.setImageResource(imageBlack);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Affiche l'image en couleur si il a été capturé sinon en nuance de gris
        private void displayImageFoodie(GalleryFoodiesActivity gallery,String nameInJson, int idImageView, int imageColor, int imageBlack){
            try {
                ImageView iw = gallery.findViewById(idImageView);
                if (isCaptured(nameInJson)){iw.setImageResource(imageColor); } else {iw.setImageResource(imageBlack);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //affiche la checkBox du foodie si il a été capturé
        private void displayCheckBox(GalleryFoodiesActivity gallery,String nameInJson, int idCheck) {
            try {
                ImageView iw = gallery.findViewById(idCheck);
                if (isCaptured(nameInJson)){ iw.setVisibility(View.VISIBLE); } else {iw.setVisibility(View.INVISIBLE);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Renvoi un boolean indiquant si le foodie a déjà été capturé selon l'information stocké dans le fichier JSON
        private Boolean isCaptured(String foodie) throws Exception {
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






public static void WriteToFile(Context context, String filename, String str){
    File file = contextApp.getFileStreamPath(filename);
    if(!file.exists()) {
        try {


            FileOutputStream fos = context.openFileOutput(filename, context.MODE_PRIVATE);
            fos.write(str.getBytes(StandardCharsets.UTF_8), 0, str.length());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    else{
        System.out.println("Le fichier n'existe pas");

        System.out.println("Un nouveau fichier a été créé");

    }
}

public void writeToFile(File f,String str) throws IOException{


            File ff=contextApp.getExternalFilesDir("captured.json");
            FileOutputStream fos= new FileOutputStream(ff);
            fos.write(str.getBytes(StandardCharsets.UTF_8));
            fos.close();

}

        /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view1, new UIComponentForGallery()).commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view2, new UIComponentForGallery()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view3, new UIComponentForGallery()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view4, new UIComponentForGallery()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view5, new UIComponentForGallery()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view6, new UIComponentForGallery()).commit();
*/


        /*        Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");

                Gson gson = new Gson();
                 final int REQUEST_WRITE_STORAGE = 1;

                String json = gson.toJson(map);
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    // Permission is already granted, proceed with file write
                    try {
                        FileWriter writer = new FileWriter("captured.json");
                    writer.write(json);
                    writer.close();
                    System.out.println("********************* SUCCES to try catch");
                } catch (IOException e) {
                    System.out.println("********************* ERROR to try catch");
                    e.printStackTrace();
                }*/
    // ********************************* DEUXIEME ESSAI
        /*// json will be a string representation of the map in JSON format, e.g. {"key1":"value1","key2":"value2"}
        // Create a HashMap object and add some key-value pairs to it
                HashMap<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");

        // Create a Gson object
                Gson gson = new Gson();

        // Convert the HashMap to a JSON string
                String jsonString = gson.toJson(map);

        // Write the JSON string to a file
                //FileOutputStream outputStream = null;
                final int REQUEST_WRITE_STORAGE = 1;

                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    System.out.println("********************* NOT THE FUCKING PERMISSION");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    System.out.println("********************* PERMISSION");
                    FileOutputStream outputStream = null;

        // Get the external files directory
                    File filesDir = getExternalFilesDir(null);

        // Create a file in the external files directory
                    File file = new File(filesDir, "isCaptured.json");

        // Write the JSON string to the file
                    try {
                        outputStream = new FileOutputStream(file);
                        //outputStream.write(jsonString.getBytes());
                        String test = "ta grand mere la pute";
                        outputStream.write(test.getBytes(StandardCharsets.UTF_8));
                        outputStream.close();
                        System.out.println("********************* SUCCES to try catch");
                    } catch (FileNotFoundException e) {
                        System.out.println("********************* Error 1 to try catch");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("********************* error 2 to try catch");
                        e.printStackTrace();
                    }
                }*/
    // ***************************** TROISIEME ESSAI
    // Get an instance of the AssetManager//
        public JSONObject getJsonObject() throws JSONException {
            // tuto :
            // https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", "name test");
            jsonObject.put("Enroll_No", 2323);
            jsonObject.put("Mobile", 0);
            jsonObject.put("Address", "adressTest");
            System.out.println("*********************** jsonObject : "+ jsonObject);
            return jsonObject;
        }
    public void writeJSON(JSONObject jsonObject, String FILE_NAME) throws IOException {
        System.out.println("*************** jsonObject : " + jsonObject);

        // Convert JsonObject to String Format
        /*String userString = jsonObject.toString();
        File file = new File(getApplicationContext().getFilesDir(),FILE_NAME);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(userString);*/


        String userString = jsonObject.toString();
        FileWriter file = new FileWriter("myjson.json");
        file.write(userString);
        file.flush();
        file.close();
/*
        // Define the File Path and its Name
        File file = new File(getApplicationContext().getFilesDir(),FILE_NAME);
        FileWriter fileWriter = new FileWriter(file);
        //FileWriter fileWriter = new FileWriter("captured.txt");
        fileWriter.write(userString);
        */
        /*BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();
        readJson("captured.json");*/
        System.out.println("*********************** writeJSON passed");
    }


}

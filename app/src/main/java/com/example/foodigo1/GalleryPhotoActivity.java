package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class GalleryPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    String foodieName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate() de GalleryPhotoActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photo);
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        manager.updatePoints(this); //mise à jour des points

        foodieName = getIntent().getExtras().getString("foodieName");
        TextView tw = findViewById(R.id.foodieName);
        tw.setText(foodieName);
        String path = null;
        manager.showThePrefrerencesInConsole("foodieName = " + foodieName + "Les préférence : ",R.string.nameOfAbsolutePathPreference);
        try {
            path = manager.getAbsolutePathFromPreference(foodieName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("chemin : ",  "path : " + path);

        // Récupérez l'ImageView dans laquelle vous souhaitez afficher les images
        ImageView photoFoodieCaptured = findViewById(R.id.photoFoodieCaptured);

        // Créez un nouveau bitmap à partir de l'image de la mangue stockée dans les drawables
        Bitmap mangueBitmap = BitmapFactory.decodeResource(getResources(), manager.getIdOfDrawableFoodie(foodieName));

        // Charger l'image du foodie à partir du disque local
        Uri imageUri = null;
        try {
            Log.e("chemin : ", manager.getAbsolutePathFromPreference(foodieName));
            imageUri = Uri.parse("file://" + manager.getAbsolutePathFromPreference(foodieName));

        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap foodieBitmap = null;
        try {
            foodieBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Effectuez une rotation de 90 degrés de l'image du foodie
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedFoodieBitmap = Bitmap.createBitmap(foodieBitmap, 0, 0, foodieBitmap.getWidth(), foodieBitmap.getHeight(), matrix, true);

        // Redimensionnez l'image de la mangue pour qu'elle ait la même largeur que l'image du foodie rotatée
        int newMangueWidth = rotatedFoodieBitmap.getWidth();
        int newMangueHeight = (int) (mangueBitmap.getHeight() * ((float) newMangueWidth / mangueBitmap.getWidth()));
        Bitmap resizedMangueBitmap = Bitmap.createScaledBitmap(mangueBitmap, newMangueWidth, newMangueHeight, true);
        // Créez un nouveau bitmap en fusionnant les deux images (mangue et foodie)
        //Bitmap mergedBitmap = mergeBitmaps(mangueBitmap, foodieBitmap);
        Bitmap mergedBitmap = mergeBitmaps(resizedMangueBitmap, rotatedFoodieBitmap);


        // Affichez le bitmap fusionné dans l'ImageView
        photoFoodieCaptured.setImageBitmap(mergedBitmap);


    }

    //TODO : cette classe ne fonctionne pas encore comme elle le devrait car version de test.
    // Version finale : lors de l'appel de cette activité elle affiche la photo qui correspond
    // au foodie si elle a été appelé depuis l'icone photo de la galerie ou permet de prendre
    // une photo si elle a été appelée depuis l'icone appareil photo de la map

    // Méthode pour fusionner deux bitmaps
    private Bitmap mergeBitmaps(Bitmap topBitmap, Bitmap bottomBitmap) {
        // Créez un nouveau bitmap de la taille de l'image de base (bottomBitmap)
        Bitmap mergedBitmap = Bitmap.createBitmap(bottomBitmap.getWidth(), bottomBitmap.getHeight(), bottomBitmap.getConfig());

        // Créez un nouvel objet Canvas en utilisant le bitmap fusionné
        Canvas canvas = new Canvas(mergedBitmap);

        // Dessinez l'image de base (bottomBitmap) sur le canvas
        canvas.drawBitmap(bottomBitmap, 0, 0, null);

        int topBitmapWidth = topBitmap.getWidth();
        int topBitmapHeight = topBitmap.getHeight();
        int bottomBitmapWidth = bottomBitmap.getWidth();
        int bottomBitmapHeight = bottomBitmap.getHeight();

// Calculer les coordonnées de départ (x, y) pour dessiner topBitmap sur le canvas
        int x = (bottomBitmapWidth - topBitmapWidth) / 2;
        int y = (bottomBitmapHeight - topBitmapHeight) / 2;

        canvas.drawBitmap(topBitmap, x, y, null);


        // Retournez le bitmap fusionné
        return mergedBitmap;
    }


    /*
    Retrourne la photo qui correspond au foodie depuis lequel l'activité à été appelé
     *//*
    private void displayPhotoInImageView(ManageFoodiesCaptured manager) {
        System.out.println("displayPhotoInImageView() de GalleryPhotoActivity");
        Intent i = getIntent();
        int callBy =  i.getExtras().getInt("callBy");
        System.out.println("callBy : " + callBy);
        if (callBy != 0) {
            switch (callBy) {
                case (R.id.ananasPicture):
                    callManagerDisplayPhotoFoodieCaptured("ananas", manager);
                case (R.id.avocatPicture):
                    callManagerDisplayPhotoFoodieCaptured("avocat", manager);
                case (R.id.bananePicture):
                    callManagerDisplayPhotoFoodieCaptured("banane", manager);
                case (R.id.pastequePicture):
                    callManagerDisplayPhotoFoodieCaptured("pasteque", manager);
                case (R.id.manguePicture):
                    callManagerDisplayPhotoFoodieCaptured("mangue", manager);
                case (R.id.pommesPicture):
                    callManagerDisplayPhotoFoodieCaptured("pommes", manager);
            }
        }
    }


    *//*
    Affiche la photo du foodie si elle est déjà prise et enregistrée
     *//*
    private void callManagerDisplayPhotoFoodieCaptured(String name, ManageFoodiesCaptured manager){
        manager.displayPhotoFoodieCaptured(this,name,R.id.photoFoodieCaptured);
    }*/


    /*
    Méthode evenementielle de gestion du clic
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.menu): //menu
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;

            case(R.id.home): //logo retour vers l'acceuil
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;

            case(R.id.back): //retour
                if (view.getParent() != null) {
                    //on redirige vers l'activité qui appelle
                    finish();
                }else{
                    //si on a perdu le prent on redirige vers l'acceuil
                    startActivity(new Intent(this, MainActivity.class));
                }
                //revenir sur l'activité appelante'
                break;

        }
    }


}
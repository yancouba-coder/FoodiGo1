package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class GalleryPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate() de GalleryPhotoActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photo);
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        manager.updatePoints(this); //mise à jour des points
        //displayPhotoInImageView(manager); //mise à jour de la photo en fonction du foodie appelant
        path = getIntent().getExtras().getString("path");
        ImageView photoFoodieCaptured = findViewById(R.id.photoFoodieCaptured);
        Uri imageUri = Uri.parse("file://" + path);

        photoFoodieCaptured.setImageURI(imageUri);
    }

    //TODO : cette classe ne fonctionne pas encore comme elle le devrait car version de test.
    // Version finale : lors de l'appel de cette activité elle affiche la photo qui correspond
    // au foodie si elle a été appelé depuis l'icone photo de la galerie ou permet de prendre
    // une photo si elle a été appelée depuis l'icone appareil photo de la map



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
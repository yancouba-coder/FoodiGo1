package com.example.foodigo1;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.security.identity.CipherSuiteNotSupportedException;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GalleryFoodiesActivity extends AppCompatActivity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_foodies);
        manager.displayCapturedFoodie(this);
        manager.updatePoints(this); //mise à jour des points
    }




    @Override
    public void onClick(View view) {
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        Intent photo = null;
        switch (view.getId()) {
            case (R.id.play):
                Intent i = null;
                if(manager.gameIsComplete()){
                    i = new Intent(this, GameCompleteActivity.class);
                } else{
                    i = new Intent(this, Maps3Activity.class);
                }
                if(i!=null){startActivity(i);}
                break;
            case(R.id.home):

                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;

                //Affichage du menu
            case (R.id.menu):
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;

                //Affichage de l'image du fruit si elle existe
            case (R.id.ananasPicture):
                if (!(manager.isCaptured("ananas"))){ //le foodie n'est pas capturé donc il n'a pas de photo
                    Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
                }else{ //le foodie doit avoir une photo
                    photo = new Intent(this, GalleryPhotoActivity.class);
                }
                break;
            case(R.id.avocatPicture):
                if (!(manager.isCaptured("avocat"))){ //le foodie n'est pas capturé donc il n'a pas de photo
                    Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
                }else{ //le foodie doit avoir une photo
                    photo = new Intent(this, GalleryPhotoActivity.class);
                }
                break;
            case(R.id.bananePicture):
                if (!(manager.isCaptured("banane"))){ //le foodie n'est pas capturé donc il n'a pas de photo
                    Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
                }else{ //le foodie doit avoir une photo
                    photo = new Intent(this, GalleryPhotoActivity.class);
                }
                break;
            case(R.id.pastequePicture):
                if (!(manager.isCaptured("pasteque"))){ //le foodie n'est pas capturé donc il n'a pas de photo
                    Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
                }else{ //le foodie doit avoir une photo
                    photo = new Intent(this, GalleryPhotoActivity.class);
                }
                break;
            case(R.id.manguePicture):
                if (!(manager.isCaptured("mangue"))){ //le foodie n'est pas capturé donc il n'a pas de photo
                    Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
                }else{ //le foodie doit avoir une photo
                    photo = new Intent(this, GalleryPhotoActivity.class);
                }
                break;
            case(R.id.pommesPicture):
                if (!(manager.isCaptured("pommes"))){ //le foodie n'est pas capturé donc il n'a pas de photo
                    Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
                }else{ //le foodie doit avoir une photo
                    photo = new Intent(this, GalleryPhotoActivity.class);
                }
                break;
            default:
                return;

        }
        if(photo != null){
            photo.putExtra("callBy",view.getId());
            startActivity(photo);
        }
    }


}
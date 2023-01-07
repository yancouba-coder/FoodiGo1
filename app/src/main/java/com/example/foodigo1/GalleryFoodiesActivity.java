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
        String foodieName = null;
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
                foodieName = "ananas";
                break;
            case(R.id.avocatPicture):
                foodieName = "avocat";
                break;
            case(R.id.bananePicture):
                foodieName = "banane";
                break;
            case(R.id.pastequePicture):
                foodieName = "pasteque";
                break;
            case(R.id.manguePicture):
                foodieName = "mangue";
                break;
            case(R.id.pommesPicture):
                foodieName = "pommes";
                break;
            default:
                return;

        }
        if (foodieName != null){
            if (!(manager.isCaptured(foodieName))){ //le foodie n'est pas capturé donc il n'a pas de photo
                Toast.makeText(this, R.string.messageErrorFoodieNotcaptured, Toast.LENGTH_LONG).show();
            }else{ //le foodie doit avoir une photo
                photo = new Intent(this, GalleryPhotoActivity.class);
                try {
                    photo.putExtra("path",manager.getAbsolutePathFromPreference(foodieName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(photo != null){
            photo.putExtra("callBy",view.getId());
            startActivity(photo);
        }
    }


}
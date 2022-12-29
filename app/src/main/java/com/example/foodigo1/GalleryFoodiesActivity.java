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

    }




    @Override
    public void onClick(View view) {
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());

        switch (view.getId()) {
            case (R.id.play):
                Intent map = new Intent(this, Maps3Activity.class);
                startActivity(map);

            case(R.id.home):

                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);


                //Affichage du menu
            case (R.id.menu):
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;

                //Affichage de l'image du fruit si elle existe
            case (R.id.ananasPicture):
            case(R.id.avocatPicture):
            case(R.id.bananePicture):
            case(R.id.pastequePicture):
            case(R.id.manguePicture):
            case(R.id.pommesPicture):
                Intent photo = new Intent(this, GalleryPhotoActivity.class);
                photo.putExtra("callBy",view.getId());
                startActivity(photo);
                break;
            default:
                return;
        }
    }


}
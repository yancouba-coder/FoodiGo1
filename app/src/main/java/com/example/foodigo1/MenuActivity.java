package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    @Override
    public void onClick(View view) {
        Intent i=null;

        switch (view.getId()){

            case R.id.logo:
                i = new Intent(this, MainActivity.class);
                break;
            case R.id.menu:
                //String callBy = getIntent().getStringExtra("callBy");

                if (view.getParent() != null) {
                    //on redirige vers l'activité qui appelle
                    finish();

                }else{
                    //si on a perdu le prent on redirige vers l'acceuil
                    i = new Intent(this, MainActivity.class);
                }
                //revenir sur l'activité appelante'
                break;
            case R.id.foodies:
                i = new Intent(this, GalleryFoodiesActivity.class);
                break;
            case R.id.map:
                i = new Intent(this, Maps1Activity.class);
                break;
            case R.id.photos:
                i = new Intent(this, GalleryPhotoActivity.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());

        }
        if (i !=null) startActivity(i);
    }
    //logo, menu, foodies, map, photos

}
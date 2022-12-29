package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Maps3Activity extends AppCompatActivity implements View.OnClickListener {
    //Officiel Maps activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        Fragment fragment= new Map_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragCarte,fragment).commit();
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(this);
        manager.displayCapturedFoodieForMapsActivity(this);
        manager.updatePoints(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()){

            case R.id.menu:
                 i = new Intent(this, MenuActivity.class);

            case R.id.photo_ico_black:
                 i = new Intent(this, PhotoActivity.class);

            case R.id.arrow_left_black:
            case R.id.grid_ico_black:
                i = new Intent(this, GalleryFoodiesActivity.class);

            case R.id.home:
                i = new Intent(this, MainActivity.class);
            default:
                break;

        }
        if (i !=null) startActivity(i);
    }
}
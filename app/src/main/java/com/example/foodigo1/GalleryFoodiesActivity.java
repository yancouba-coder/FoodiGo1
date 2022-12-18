package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GalleryFoodiesActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, UIComponentForGallery.class, null)
                    .commit();
        }
    }
}
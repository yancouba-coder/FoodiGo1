package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class GameCompleteActivity extends AppCompatActivity implements View.OnClickListener {
    ManageFoodiesCaptured manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_complete);
        manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        manager.updatePoints(this); //mise à jour des points
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.replay):
                manager.reInitPreferences();
                Toast.makeText(this, R.string.replayMessage, Toast.LENGTH_LONG).show();
                Intent gallery = new Intent(this, GalleryFoodiesActivity.class);
                startActivity(gallery);
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
            default:
                return;

        }
    }
}
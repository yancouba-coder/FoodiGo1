package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(this);
        manager.initPreferences(); //Initialise les variables à false si elles n'existent pas déjà
        manager.writeToPreferences("avocat", true);
        manager.updatePoints(this);
    }

    @Override
    public void onClick(View view) {
        //code provisoire pour test du menu
        System.out.println("le jeu commence");
        Intent menu = new Intent(this, MenuActivity.class);

//comment
        System.out.println("***************************** this.getLocalClassName() : " + this.getLocalClassName());
        menu.putExtra("callBy",this.getLocalClassName());
        startActivity(menu);

    }
}
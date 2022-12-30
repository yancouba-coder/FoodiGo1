package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ManageFoodiesCaptured manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         manager = ManageFoodiesCaptured.getInstance(this);
        manager.initPreferences(); //Initialise les variables à false si elles n'existent pas déjà
        //TODO : a effacer dans la version de deploiement, c'est pour le test
        manager.writeToPreferences("avocat", true);
        manager.writeToPreferences("mangue", true);
        // FIN DU TODO

        manager.updatePoints(this);
    }

    @Override
    public void onClick(View view) {
        /*
        //Pour test : marque tout les foodies comme capturé et permet de réinitialisé le jeu
        List<String> foodieNames = manager.getListOfFoodies();

        for (String foodie : foodieNames){
            manager.writeToPreferences(foodie, true);
        }
        */
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
            default:
                return;

        }



    }
}
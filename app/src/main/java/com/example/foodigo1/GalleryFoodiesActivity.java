package com.example.foodigo1;



import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;



public class GalleryFoodiesActivity extends AppCompatActivity implements View.OnClickListener {
    private ManageFoodiesCaptured manager;
    protected void onCreate(Bundle savedInstanceState) {
        manager = ManageFoodiesCaptured.getInstance(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_foodies);
        manager.displayCapturedFoodie(this);
        manager.updatePoints(this); //mise à jour des points

        if (manager.gameIsComplete()) {
            Button button = findViewById(R.id.play);
            button.setText("Suivant");
        }
    }

    /*
    Si le jeu et fini le bouton jouer devient suivant
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (manager.gameIsComplete()) {
            Button button = findViewById(R.id.play);
            button.setText("Suivant");
        }
    }


    /*
    Lié à l'evenement du clic sur l'UI
     */
    @Override
    public void onClick(View view) {
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        Intent photo = null;
        String foodieName = null;
        switch (view.getId()) {
            case (R.id.play): // bouton jouer ou suivant
                Intent i = null;
                if(manager.gameIsComplete()){
                    i = new Intent(this, GameCompleteActivity.class);
                } else{
                    i = new Intent(this, Maps3Activity.class);
                }
                if(i!=null){startActivity(i);}
                break;
            case(R.id.home): //logo
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;


            case (R.id.menu): //Affichage du menu
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
                    photo.putExtra("foodieName",foodieName);
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
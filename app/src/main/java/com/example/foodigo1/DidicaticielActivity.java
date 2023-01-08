package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DidicaticielActivity extends AppCompatActivity implements View.OnClickListener {
    int clickNumber;
    private Button next;
    private TextView description;
    private ImageView imageViewDidac;
    private TextView title;
    private TextView subtitle;
    private String totemName;
    private Button previous;
    ManageFoodiesCaptured manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_didicaticiel);
        clickNumber = 0;
        manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        manager.updatePoints(this); //mise à jour des points
        next = findViewById(R.id.next);
        description = findViewById(R.id.description);
        imageViewDidac = findViewById(R.id.imageViewDidac);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);

        SharedPreferences sharedPref = this.getSharedPreferences(String.valueOf(R.string.nameOfDidacticiel), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Boolean didacAlreadyShow = sharedPref.getBoolean("didac",false);
        editor.putBoolean("didac", true);
        editor.apply();
        manager.showThePrefrerencesInConsole("Préférences : Didaccticiel = true", R.string.nameOfDidacticiel);

        //TODO : instancer le totem depuis les préférence
        totemName = "totem";
        previous = findViewById(R.id.previous);
        previous.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                clickNumber++;
                changeUI();
                break;
            case R.id.previous:
                clickNumber--;
                changeUI();
                break;
            case R.id.menu:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.home:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }

    public void changeUI(){
        String nextString = "Suivant";
        String titleString = "Didacticiel";
        String subtitleString = "";
        String descriptionString = "";
        int idImage = 0;
        switch (clickNumber){

            case 0:
                subtitleString =  this.getString(R.string.stepToPlayMessage);
                descriptionString = "";
                idImage = R.drawable.level_up;
                previous.setVisibility(View.INVISIBLE);
                break;
            case 1:
                previous.setVisibility(View.VISIBLE);
                subtitleString = "Tu vas devoir rechercher des foodies sur la carte et les capturer pour nourrir ton " + totemName;
                descriptionString = "Commence le jeu en cliquant sur jouer";
                idImage = R.drawable.home;
                break;
            case 2:
                subtitleString = "Tu peux voir dans la galerie quels sont les foodies que tu as déjà capturés.";
                descriptionString = "Pour le moment, tu n'en as capturé aucun, tu devras cliquer sur Jouer pour commencer.";
                idImage = R.drawable.gallery_empty;
                break;
            case 3:
                subtitleString = "La carte te permet de voir où se situe les foodies.";
                descriptionString = "Rapproche toi de l'un d'eux pour le capturer. Les foodies qui rapportent le plus de points sont aussi les plus loins !";
                idImage = R.drawable.map;
                break;
            case 4:
                subtitleString = "Quand tu t'es suffiisament rapproché du foodie, tu pourras le prendre en photo ! La distance de capture est personnalisable";
                descriptionString = "Sur 'Prendre une photo', tu cliqueras pour le capturer !";
                idImage = R.drawable.distance_banane;
                break;
            case 5:
                subtitleString = "Le but est de voir le fooodie s'afficher !";
                descriptionString = "Tu devras tourner sur toi même et dirigier ton téléphone vers le foodie pour qu'il s'affiche.";
                idImage = R.drawable.oncapture_banane;
                break;
            case 6:
                subtitleString = "Au fur et à mesure que tu captures des foodies tu les verras s'afficher dans la galerie";
                descriptionString = "Depuis la galerie tu pourras voir combien de points tu as gagné. Tu pourras aussi accéder à la photo en cliquant sur l'icone de la photo.";
                idImage = R.drawable.gallery_running;
                break;
            case 7:
                subtitleString = "Tu peux visualiser les photos que tu as pris des foodies";
                descriptionString = "Clique sur retour pour revenir à la galerie.";
                idImage = R.drawable.photo_banane;
                break;
            case 8:
                subtitleString = "Plus tu gagnes de points, plus tu donnes à manger à ton " + totemName + ".";
                descriptionString = "Gagnes des points pour faire grandir ton " + totemName + ".";
                idImage = R.drawable.home;
                break;
            case 9:
                subtitleString = "Depuis n'importe quel écran du jeu, tu peux accéder au menu en cliquant sur les 3 petits points en haut à droite.";
                descriptionString = "Tu retrouves ici toutes les fonctionnalités.";
                idImage = R.drawable.menu_final;
                break;
            case 10:
                subtitleString = "La page Distances te permet de personnaliser les distances maximum  et minimum pour définir ta zone de jeu.";
                descriptionString = "Choisis une nouvelle valeur et valide en cliquant sur modifier";
                idImage = R.drawable.settings;
                break;
            case 11:
                titleString = "C'est parti !";
                subtitleString = "Tu es maintenant prêt à jouer, à capturer des foodies et à voir ton " + totemName + " grandir.";
                descriptionString = "";
                nextString = "Jouer";
                idImage = manager.getIDDrawableTotem();
                //TODO : mettre le totem : idImage =
                break;
            case 12:

                idImage = -1;
                clickNumber = 0;
                break;
            default:
                break;
        }
        next.setText(nextString);
        title.setText(titleString);
        subtitle.setText(subtitleString);
        description.setText(descriptionString);

        if (idImage != -1) {
            imageViewDidac.setImageResource(idImage);
        }else{
            imageViewDidac.setImageBitmap(null);
        }

        if (clickNumber == 0 && idImage==-1 ){// cas de la fin du didacticiel
            this.finish();
        }

/*        ;

        R.id.imageViewDidac;
        ;
        R.id.subtitle;*/


    }
}
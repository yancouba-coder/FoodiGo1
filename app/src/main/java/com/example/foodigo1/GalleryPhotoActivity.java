package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class GalleryPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photo);
        Intent i = getIntent();
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        int callBy =  i.getExtras().getInt("callBy");

        switch (callBy){
            case(R.id.ananasPicture):

                manager.displayPhotoFoodieCaptured(this,"ananas",R.id.photoFoodieCaptured);

            case(R.id.avocatPicture):
            case(R.id.bananePicture):
            case(R.id.pastequePicture):
            case(R.id.manguePicture):
            case(R.id.pommesPicture):
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.menu):
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;

            case(R.id.home):
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;

            case(R.id.back):
                if (view.getParent() != null) {
                    //on redirige vers l'activité qui appelle
                    finish();

                }else{
                    //si on a perdu le prent on redirige vers l'acceuil
                    startActivity(new Intent(this, MainActivity.class));
                }
                //revenir sur l'activité appelante'
                break;

        }
    }
}
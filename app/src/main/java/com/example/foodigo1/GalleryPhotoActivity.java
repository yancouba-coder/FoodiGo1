package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GalleryPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photo);
        Intent i = getIntent();
        return;
        /*int callBy =  i.getExtras().getInt("callBy");
        System.out.println("callBy : " + callBy);
        //if (!(callBy == null)) {
            switch (callBy) {
                case (R.id.ananasPicture):
                    callManagerDisplayPhotoFoodieCaptured("ananas");
                case (R.id.avocatPicture):
                    callManagerDisplayPhotoFoodieCaptured("avocat");
                case (R.id.bananePicture):
                    callManagerDisplayPhotoFoodieCaptured("banane");
                case (R.id.pastequePicture):
                    callManagerDisplayPhotoFoodieCaptured("pasteque");
                case (R.id.manguePicture):
                    callManagerDisplayPhotoFoodieCaptured("mangue");
                case (R.id.pommesPicture):
                    callManagerDisplayPhotoFoodieCaptured("pommes");
            }
        //}


         */
    }
    private void callManagerDisplayPhotoFoodieCaptured(String name){
        ManageFoodiesCaptured manager = ManageFoodiesCaptured.getInstance(getApplicationContext());
        manager.displayPhotoFoodieCaptured(this,name,R.id.photoFoodieCaptured);
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
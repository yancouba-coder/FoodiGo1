package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class GalleryPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photo);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.menu):
                Intent menu = new Intent(this, MenuActivity.class);
                System.out.println("***************************** this.getLocalClassName() : " + this.getLocalClassName());
                menu.putExtra("callBy",this.getLocalClassName());
                startActivity(menu);
                break;

            case(R.id.home):
                Intent main = new Intent(this, MainActivity.class);
                System.out.println("***************************** this.getLocalClassName() : " + this.getLocalClassName());
                main.putExtra("callBy",this.getLocalClassName());
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
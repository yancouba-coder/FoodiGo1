package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ShowTotemActivity extends AppCompatActivity implements View.OnClickListener{

    private ManageFoodiesCaptured manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_totem);
        manager= ManageFoodiesCaptured.getInstance(this);
        int yourPoints= manager.getPoints();
        Drawable yourTotemState =manager.getDrawaleByPoints(manager.getTotem(), yourPoints);
        ImageView imageView=findViewById(R.id.totemImage);
        imageView.setImageDrawable(yourTotemState);
        manager.updatePoints(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu:
                Intent i = new Intent(this, MenuActivity.class);
                finish();
                startActivity(i);
                break;
            case R.id.next:
                finish();
                startActivity(new Intent(this,GalleryFoodiesActivity.class));
                break;
            default:
                break;

        }
    }
}
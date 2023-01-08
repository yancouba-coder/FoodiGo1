package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TotemClickedShown extends AppCompatActivity implements View.OnClickListener {

    private ManageFoodiesCaptured manager;
    String totemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totem_clicked_shown);
        Intent i=getIntent();
        totemName= i.getExtras().getString("totemName");
        int totemId=i.getExtras().getInt("totemId");
        ImageView imageView=findViewById(R.id.totemImage);
        manager= ManageFoodiesCaptured.getInstance(this);
        imageView.setImageDrawable(manager.getTotemDrawable(totemId));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                Intent i= new Intent(this,Maps3Activity.class);
                if(totemName!=null){
                    manager.writeToPreferenceTotem(totemName);
                }
                startActivity(i);
                break;
            case R.id.menu:
                Intent ii = new Intent(this, MenuActivity.class);
                startActivity(ii);
                break;

            default:
                break;
        }
    }
}
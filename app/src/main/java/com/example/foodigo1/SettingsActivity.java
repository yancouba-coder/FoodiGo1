package com.example.foodigo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    EditText maxdistance;
    EditText mindistance;
    ManageFoodiesCaptured manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
       maxdistance=findViewById(R.id.maxDistance);
        mindistance=findViewById(R.id.minDistance);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.modifier:

                int maxdist=Integer.parseInt(String.valueOf(maxdistance.getText()));
                int mindist=Integer.parseInt(String.valueOf(mindistance.getText()));

                manager.writeToPreferencesDistance(maxdist,mindist);
                Toast.makeText(this,"Modification effectuée",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu:
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
                break;
            case R.id.logo:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            default:
                break;


        }

    }

    @Override
    protected void onResume() {
        manager=ManageFoodiesCaptured.getInstance(this);
        maxdistance.setHint(""+manager.getDistance("MaximumDistance"));
        mindistance.setHint(""+manager.getDistance("MinimumDistance"));
        super.onResume();
    }
}
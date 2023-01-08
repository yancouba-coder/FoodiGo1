package com.example.foodigo1;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserTotemActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnDisplay;
    List<RadioButton> checkedRadioButtons;
    private UserTotemActivity act;

    ManageFoodiesCaptured manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_totem);
        checkedRadioButtons = new ArrayList<RadioButton>();
        act=this;
        manager= ManageFoodiesCaptured.getInstance(this);


    }


    @Override
    public void onClick(View v) {
        String totemName;
        Intent i1;
        switch (v.getId()){
            case R.id.menu:
               Intent i = new Intent(this, MenuActivity.class);
               startActivity(i);
                break;
            case R.id.buldogImage:
               totemName =manager.getTotemNameWithTotemid(R.id.buldogImage);
                i1= new Intent(this,TotemClickedShown.class);
                i1.putExtra("totemName",totemName);
                i1.putExtra("totemId",R.id.buldogImage);
                startActivity(i1);
                break;
            case R.id.elephantImage:
                totemName =manager.getTotemNameWithTotemid(R.id.elephantImage);
                i1= new Intent(this,TotemClickedShown.class);
                i1.putExtra("totemName",totemName);
                i1.putExtra("totemId",R.id.elephantImage);
                startActivity(i1);
                break;
            case R.id.Giraffe:
                totemName =manager.getTotemNameWithTotemid(R.id.Giraffe);
                i1= new Intent(this,TotemClickedShown.class);
                i1.putExtra("totemName",totemName);
                i1.putExtra("totemId",R.id.Giraffe);
                startActivity(i1);
                break;
            case R.id.horseImage:
                totemName =manager.getTotemNameWithTotemid(R.id.horseImage);
                i1= new Intent(this,TotemClickedShown.class);
                i1.putExtra("totemName",totemName);
                i1.putExtra("totemId",R.id.horseImage);
                startActivity(i1);
                break;
            case  R.id.cochonImage:
                totemName =manager.getTotemNameWithTotemid(R.id.cochonImage);
                i1= new Intent(this,TotemClickedShown.class);
                i1.putExtra("totemName",totemName);
                i1.putExtra("totemId",R.id.cochonImage);
                startActivity(i1);
                break;
            case R.id.LicorneImage:
                totemName =manager.getTotemNameWithTotemid(R.id.LicorneImage);
                i1= new Intent(this,TotemClickedShown.class);
                i1.putExtra("totemName",totemName);
                i1.putExtra("totemId",R.id.LicorneImage);
                startActivity(i1);
                break;

            default:
                break;

        }

    }






/*

    public boolean isOneRadioButton(){
        RadioButton buldogRad=findViewById(R.id.buldogRadioButton);


         RadioButton elephantRad=  findViewById(R.id.Radioelefant_1);
        RadioButton girafferRad=findViewById(R.id.RadioGiraffe);
        RadioButton horseRad=findViewById(R.id.RadioHorse);
        RadioButton cochonRad=findViewById(R.id.RadioCochon);
                RadioButton lcorneRad=findViewById(R.id.RadioLicorne);

        RadioButton[] radioButtons = { buldogRad,elephantRad, girafferRad,horseRad,cochonRad,lcorneRad };
        for (RadioButton radioButton : radioButtons) {
            if (radioButton.isChecked()) {
                checkedRadioButtons.add(radioButton);
            }
        }
        if(checkedRadioButtons.size()>1||checkedRadioButtons.isEmpty())
            return false;
        return true;





    }
    public void showPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Totem").setMessage("Vous devez choisir 1 totem");
        AlertDialog dialog=builder
                .setPositiveButton("Ok j'ai compris", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reloadActivity();
                    }
                }).create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        // AlertDialog dialog = builder.create();
        dialog.show();
}
    public void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
    public String getTotemName(int buttonRadId){
        String result="";
        switch (buttonRadId){
            case R.id.buldogRadioButton:
                result="Buldog";
            case R.id.Radioelefant_1:
                result= "Elephant";
            case R.id.RadioGiraffe:
               result= "Giraffe";
            case R.id.RadioHorse:
                result= "Cheval";
            case R.id.RadioCochon:
                result= "Cochon";
            case R.id.RadioLicorne:
                result= "Licorne";
        }
        return result;

    }

*/
}
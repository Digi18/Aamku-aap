package com.app.aamku;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class ChooseLogin extends AppCompatActivity {

    CheckBox checkSale,checkRetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login);

        ActionBar ab = getSupportActionBar();
        assert ab!= null;
        ab.hide();

        checkSale = findViewById(R.id.checkSale);
        checkRetail = findViewById(R.id.checkRetail);

        checkSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    Intent i = new Intent(ChooseLogin.this,Login.class);
                    i.putExtra("type","Salesperson");
                    startActivity(i);
                    finish();
                }

            }
        });

        checkRetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    Intent i = new Intent(ChooseLogin.this,Login.class);
                    i.putExtra("type","Retailer");
                    startActivity(i);
                    finish();
                }

            }
        });
    }
}

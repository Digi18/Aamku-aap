package com.app.aamku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenerateOrder extends AppCompatActivity {

    EditText partyName,address,gst,day,month,year,vendor;
    Button generateClient,generateOrder;

    FirebaseAuth fAuth;

    private static final String URL = "https://aamku.herokuapp.com/generateClient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_order);

        fAuth = FirebaseAuth.getInstance();

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle("Generate order");
        ab.setDisplayHomeAsUpEnabled(true);

        partyName = findViewById(R.id.partyName);
        address = findViewById(R.id.address);
        gst = findViewById(R.id.gst);
        day = findViewById(R.id.day);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        vendor = findViewById(R.id.vendor);
        generateClient = findViewById(R.id.generateClient);
        generateOrder = findViewById(R.id.generateOrder);

        generateOrder.setEnabled(false);

        year.setText("2019");

        generateClient.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                saveData();
            }
        });

        generateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(GenerateOrder.this,SelectPack.class);
                startActivity(in);
                finish();
            }
        });

    }

    private void saveData(){

        final ProgressDialog prg = new ProgressDialog(GenerateOrder.this);
        prg.setMessage("Generating client...");
        prg.setCancelable(false);
        prg.show();

        final String str1 = partyName.getText().toString();
        String str2 = address.getText().toString();
        String str3 = gst.getText().toString();
        String str4 = day.getText().toString();
        String str5 = month.getText().toString();
        String str6 = vendor.getText().toString();

        if(str1.equals("")){

            Toast.makeText(getApplicationContext(),"Enter party name",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str2.equals("")){

            Toast.makeText(getApplicationContext(),"Enter address",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str3.equals("")){

            Toast.makeText(getApplicationContext(),"Enter GST no.",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str4.equals("")){

            Toast.makeText(getApplicationContext(),"Enter day",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str4.length() > 2){

            Toast.makeText(getApplicationContext(),"Enter valid day",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str5.equals("")){

            Toast.makeText(getApplicationContext(),"Enter month",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str5.length() > 2){

            Toast.makeText(getApplicationContext(),"Enter valid month",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else if(str6.equals("")){

            Toast.makeText(getApplicationContext(),"Enter vendor",Toast.LENGTH_SHORT).show();
            prg.dismiss();
        }
        else{

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20,TimeUnit.SECONDS)
                    .writeTimeout(20,TimeUnit.SECONDS)
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("name",str1)
                    .add("address",str2)
                    .add("gst",str3)
                    .add("day",str4)
                    .add("month",str5)
                    .add("vendor",str6)
                    .build();

            Request request = new Request.Builder().post(formBody).url(URL).build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                String res = response.body().string();

                                if(res.equals("Client generated")){

                                    generateOrder.setEnabled(true);

                                    Toast.makeText(getApplicationContext(),res,Toast.LENGTH_SHORT).show();

                                    partyName.setText("");
                                    address.setText("");
                                    gst.setText("");
                                    day.setText("");
                                    month.setText("");
                                    vendor.setText("");

                                    prg.dismiss();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull final IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            prg.dismiss();
                        }
                    });
                }

            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch(id){

            case android.R.id.home:

                Intent i = new Intent(GenerateOrder.this,Dashboard.class);
                startActivity(i);
                finish();
                break;

            case R.id.action_account:

                AlertDialog.Builder builder = new AlertDialog.Builder(GenerateOrder.this);
                builder.setMessage("Are you sure you want to logout.");
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent in =new Intent(GenerateOrder.this,MainActivity.class);
                        startActivity(in);
                        fAuth.signOut();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GenerateOrder.this);
        builder.setMessage("Do you want to exit.");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

               finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}

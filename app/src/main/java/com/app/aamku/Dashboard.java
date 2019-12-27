package com.app.aamku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseUser user;
    String message;
    CardView card1,card2,card3,card4,card5,card6;
    LinearLayout layout1,layout2,layout3;
    ProgressBar progressBar;

    private static final String URL = "https://aamku.herokuapp.com/checkRole";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle("Dashboard");

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        progressBar = findViewById(R.id.progressBar);


     //   checkRoleType();

    /*    Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            message = bundle.getString("type");

            if(message.equals("Retailer")) {

                card5.setVisibility(View.INVISIBLE);
                card6.setVisibility(View.INVISIBLE);
            }
        }  */

        if(user != null){

            checkRoleType();

        } else{

            Intent i = new Intent(Dashboard.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Dashboard.this,GenerateOrder.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    private void checkRoleType(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("id",user.getUid())
                .build();

        Request request = new Request.Builder().post(formBody).url(URL).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            JSONArray jsonArray = new JSONArray(response.body().string());

                            for(int i=0;i<jsonArray.length();i++){

                                JSONObject object = jsonArray.getJSONObject(i);

                                String str1 = object.getString("type");

                                if(str1.equals("Retailer")){

                                    layout1.setVisibility(View.VISIBLE);
                                    layout2.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else{

                                    layout1.setVisibility(View.VISIBLE);
                                    layout2.setVisibility(View.VISIBLE);
                                    layout3.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (JSONException e) {
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
                    }
                });
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch(id){

            case R.id.action_account:

                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
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

                        fAuth.signOut();

                        Intent in =new Intent(Dashboard.this,MainActivity.class);
                        startActivity(in);
                        finish();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
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

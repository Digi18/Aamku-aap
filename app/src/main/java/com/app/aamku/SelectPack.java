package com.app.aamku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Adapters.ProductAdapter;
import Model.ProductsModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelectPack extends AppCompatActivity {

    FirebaseAuth fAuth;
    Spinner marketSpinner;
    ProgressBar progress;
    RecyclerView products;
    List<ProductsModel> productList;
    TextView totalCost;

    private static final String URL = "https://aamku.herokuapp.com/getProducts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pack);

        fAuth = FirebaseAuth.getInstance();

        ActionBar ab = getSupportActionBar();
        assert ab!= null;
        ab.setTitle("Select Pack");
        ab.setDisplayHomeAsUpEnabled(true);

        marketSpinner = findViewById(R.id.marketSpinner);
        progress = findViewById(R.id.progress);
        products = findViewById(R.id.products);
        totalCost = findViewById(R.id.totalCost);

        products.setHasFixedSize(true);
        products.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();


        List<String> categories = new ArrayList<String>();
        categories.add("Select market");
        categories.add("Crown");
        categories.add("Long Book A4");
        categories.add("Long Book");
        categories.add("Crown Junior");
        categories.add("Physics");
        categories.add("Chemistry");
        categories.add("Biology");
        categories.add("Universal");
        categories.add("Sketch Book");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marketSpinner.setAdapter(dataAdapter);

        marketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String item = adapterView.getItemAtPosition(i).toString();

                if(item.equals("Select market")){
                    progress.setVisibility(View.INVISIBLE);
                }
                else{

                    getData(item);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        LocalBroadcastManager.getInstance(SelectPack.this).registerReceiver(message,new IntentFilter("msg"));
    }

    private void getData(String item){

         progress.setVisibility(View.VISIBLE);
         products.setVisibility(View.INVISIBLE);

         productList.clear();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("name",item)
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

                            if(jsonArray.length() > 0){

                               products.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.INVISIBLE);

                            }

                            for(int i=0;i<jsonArray.length();i++){

                                progress.setVisibility(View.INVISIBLE);


                                JSONObject object = jsonArray.getJSONObject(i);

                                String str1 = object.getString("market");
                                String str2 = object.getString("product_no");
                                String str3 = object.getString("page");
                                String str4 = object.getString("mrp");
                                String str5 = object.getString("inner_pack");
                                String str6 = object.getString("outer_pack");

                                Log.d("prod",str2);

                                ProductsModel model = new ProductsModel(str1,str2,str3,str4,str5,str6);

                                productList.add(model);
                            }

                            ProductAdapter adapter = new ProductAdapter(SelectPack.this,productList);
                            products.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
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

                        progress.setVisibility(View.INVISIBLE);
                        products.setVisibility(View.INVISIBLE);

                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

    }

    public BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String nam = intent.getStringExtra("cost");

              if(nam != null){

                  int val = Integer.parseInt(nam);

                 // int cnt = val + 10;

                  totalCost.setText("Total: "+val+".00");

              }

        }
    };

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

                Intent i = new Intent(SelectPack.this,Dashboard.class);
                startActivity(i);
                finish();
                break;

            case R.id.action_account:

                AlertDialog.Builder builder = new AlertDialog.Builder(SelectPack.this);
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

                        Intent in =new Intent(SelectPack.this,MainActivity.class);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPack.this);
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

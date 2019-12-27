package com.app.aamku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

public class Login extends AppCompatActivity {

    EditText username,pwd;
    Button makeLogin;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String message;
    ProgressDialog prg;

    String uid;

    private static final String URL = "https://aamku.herokuapp.com/saveSalesperson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();


        ActionBar ab = getSupportActionBar();
        assert ab!= null;
        ab.hide();

        Bundle bundle = getIntent().getExtras();
        message = bundle.getString("type");

        username = findViewById(R.id.username);
        pwd = findViewById(R.id.pwd);
        makeLogin = findViewById(R.id.makeLogin);

        makeLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                prg = new ProgressDialog(Login.this);
                prg.setMessage("Logging in");
                prg.setCancelable(false);
                prg.show();

                final String str1 = username.getText().toString();
                final String str2 = pwd.getText().toString();

                StringBuilder mail = new StringBuilder(str1);
                mail.append("@gmail.com");
                String email = mail.toString().toLowerCase();

                if(str1.equals("")){

                    Toast.makeText(getApplicationContext(),"Enter username",Toast.LENGTH_SHORT).show();
                    prg.dismiss();
                }
                else if(str2.equals("")){

                    Toast.makeText(getApplicationContext(),email,Toast.LENGTH_SHORT).show();
                    prg.dismiss();
                }
                else{

                    fAuth.signInWithEmailAndPassword(email,str2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {

                         if(task.isSuccessful()) {

                             user = fAuth.getCurrentUser();

                             if(user != null){

                                 uid = user.getUid();

                             }

                             saveData(str1,str2,uid);
                         }

                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {

                         Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                         prg.dismiss();
                     }
                 });
                }
            }
        });
    }

    private void saveData(String str1,String str2,String uid){

        OkHttpClient client = new OkHttpClient.Builder()
                              .connectTimeout(20, TimeUnit.SECONDS)
                              .readTimeout(20,TimeUnit.SECONDS)
                              .writeTimeout(20,TimeUnit.SECONDS)
                              .build();


        RequestBody formBody = new FormBody.Builder()
                .add("username",str1)
                .add("password",str2)
                .add("id",uid)
                .add("type",message)
                .build();

        Request request = new Request.Builder().post(formBody).url(URL).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            String resp = response.body().string();

                            if(resp.equals("User exists")){

                                Intent i = new Intent(Login.this, Dashboard.class);
                                i.putExtra("type",message);
                                startActivity(i);
                                finish();

                                prg.dismiss();
                            }
                            else if(resp.equals("User created")){

                                Intent i = new Intent(Login.this, Dashboard.class);
                                i.putExtra("type",message);
                                startActivity(i);
                                finish();

                                prg.dismiss();
                            }
                            else{

                                Intent i = new Intent(Login.this, Dashboard.class);
                                i.putExtra("type",message);
                                startActivity(i);
                                finish();

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

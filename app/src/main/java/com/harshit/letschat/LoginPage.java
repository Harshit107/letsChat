package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {


    FirebaseAuth mAuth;
    EditText userEmail;
    EditText userPassword;
    Button login;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //init
        mAuth = FirebaseAuth.getInstance();
        userPassword = findViewById(R.id.password);
        userEmail = findViewById(R.id.email);
        login = findViewById(R.id.login);
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait..");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()) {

                    loginUser();

                }
            }
        });

    }

    public boolean validate() {

        if(userEmail.getText().toString().isEmpty()){
            userEmail.setError("Field required");
            return false;
        }
        if(userPassword.getText().toString().isEmpty()){
            userPassword.setError("Field required");
            return false;
        }

        return true;
    }

    public void loginUser() {
        String emailID = userEmail.getText().toString();
        String pass = userPassword.getText().toString();
        pd.show();
        mAuth.signInWithEmailAndPassword(emailID, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(it);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }
}
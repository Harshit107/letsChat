package com.harshit.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harshit.letschat.Firebase.MyDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupPage extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText userName;
    EditText userEmail;
    EditText userPassword;
    TextView login;
    EditText cPass;
    Button signup;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        //init
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userName);
        userPassword = findViewById(R.id.password);
        cPass = findViewById(R.id.cPass);
        userEmail = findViewById(R.id.email);
        signup = findViewById(R.id.btnSignUp);
        login = findViewById(R.id.btnSignIn);
        pd = new ProgressDialog(this);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    registerUser();

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(it);
            }
        });


    }

    public boolean validate() {

        if (userName.getText().toString().isEmpty()) {
            userName.setError("Field required");
            return false;
        }
        if (userEmail.getText().toString().isEmpty()) {
            userEmail.setError("Field required");
            return false;
        }
        if (userPassword.getText().toString().isEmpty()) {
            userPassword.setError("Field required");
            return false;
        }
        if (cPass.getText().toString().isEmpty()) {
            cPass.setError("Field required");
            return false;
        }

        if (!cPass.getText().toString().equals(userPassword.getText().toString())) {
            cPass.setError("Password must be same");
            userPassword.setError("password must be same");
            return false;
        }
        return true;
    }

    public void registerUser() {
        String emailID = userEmail.getText().toString();
        String pass = userPassword.getText().toString();
        String name = userName.getText().toString();
        pd.setMessage("Please wait..");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        mAuth.createUserWithEmailAndPassword(emailID, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        
                        Map<String, Object> hm = new HashMap<>();
                        hm.put("name", name);
                        hm.put("email", emailID);
                        MyDatabase.userDetail().setValue(hm)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(it);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}

//universal chat ->message -> message
//group chat
//personal chat
//how send image, document, live location
//user profile
//last seen
//typing - optional
package com.example.softwarepatternsapptobias;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register;
    private EditText eEmail, ePassword;
    private Button logIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.registerScreen);
        register.setOnClickListener(this);

        logIn = (Button) findViewById(R.id.login);
        logIn.setOnClickListener(this);

        eEmail = (EditText) findViewById(R.id.email);
        ePassword = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        //Creating switch methods for all onclick listeners
        switch (v.getId()) {
            case R.id.registerScreen:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.login:
                userLogin();
                break;
//            case R.id.nav_logout:
//                mAuth.signOut();
//                finish();
//                startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    }

    private void userLogin() {
        String email = eEmail.getText().toString().trim();
        String password = ePassword.getText().toString().trim();

        if (email.isEmpty()) {
            eEmail.setError("Email is required");
            eEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("Please provide a valid email");
            eEmail.requestFocus();
        }
        if (password.isEmpty()) {
            ePassword.setError("Password is required");
            ePassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            ePassword.setError("Password length is too short");
            ePassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Redirect to profile
                    startActivity(new Intent(MainActivity.this, MainActivity2.class));
                    //Toast.makeText(MainActivity.this,"This user was succesfully logged in " +task.getException().getMessage(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}









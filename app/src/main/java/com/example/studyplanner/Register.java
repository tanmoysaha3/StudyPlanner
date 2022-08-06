package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private static final String TAG = "Log - Register";

    EditText nameReg, emailReg, passReg, cPassReg;
    Button regB;
    TextView loginText;
    ProgressBar regPBar;
    ImageView passVisibilityReg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameReg=findViewById(R.id.nameReg);
        emailReg=findViewById(R.id.emailReg);
        passReg=findViewById(R.id.passReg);
        cPassReg=findViewById(R.id.cPassReg);
        regB=findViewById(R.id.regB);
        regPBar=findViewById(R.id.regPBar);
        loginText=findViewById(R.id.loginText);
        passVisibilityReg=findViewById(R.id.passVisibilityReg);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        passVisibilityReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passReg.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    passVisibilityReg.setImageResource(R.drawable.ic_baseline_visibility_24_white);
                    passReg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    passVisibilityReg.setImageResource(R.drawable.ic_baseline_visibility_off_24_white);
                    passReg.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        regB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameReg.getText().toString();
                String email=emailReg.getText().toString();
                String pass=passReg.getText().toString();
                String cPass=cPassReg.getText().toString();

                if(name.isEmpty()){
                    nameReg.setError("Name empty");
                    return;
                }
                if (email.isEmpty()){
                    emailReg.setError("Email empty");
                    return;
                }
                if (pass.length()<8){
                    passReg.setError("Password length need to be at least 8");
                    return;
                }
                if(!pass.equals(cPass)){
                    cPassReg.setError("Passwords don't match");
                    return;
                }
                regPBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG,"Successfully created account");
                        startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Registration process failed with "+e.getMessage());
                        Toast.makeText(Register.this, "Failed with "+e, Toast.LENGTH_SHORT).show();
                        regPBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }
}
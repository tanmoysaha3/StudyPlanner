package com.example.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private static final String TAG = "Log - Login";

    private static final int WAIT_TIME = 3 * 60 * 1000;
    private int loginAttempts = 3;

    EditText emailLogin, passLogin;
    ImageView passVisibilityLogin;
    Button loginB;
    TextView regText, resetPassText, wrongCredLogin;
    ProgressBar loginPBar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin=findViewById(R.id.emailLogin);
        passLogin=findViewById(R.id.passLogin);
        passVisibilityLogin=findViewById(R.id.passVisibilityLogin);
        loginB=findViewById(R.id.loginB);
        regText=findViewById(R.id.regText);
        resetPassText=findViewById(R.id.resetPassText);
        wrongCredLogin=findViewById(R.id.wrongCredLogin);
        loginPBar=findViewById(R.id.loginPBar);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        passVisibilityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passLogin.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    passVisibilityLogin.setImageResource(R.drawable.ic_baseline_visibility_24);
                    passLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    passVisibilityLogin.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    passLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginAttempts == 0) {
                    Toast.makeText(Login.this, "Your attempt reach 0, please wait 3 minutes to log again", Toast.LENGTH_SHORT).show();
                    return;
                }

                email=emailLogin.getText().toString().trim();
                pass=passLogin.getText().toString();

                if (email.isEmpty()){
                    emailLogin.setError("Email is required");
                    return;
                }
                if (pass.isEmpty()){
                    passLogin.setError("Password is required");
                    return;
                }
                loginPBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG,"Successfully logged in");
                        loginPBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(getApplicationContext(),ReminderList.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Login Failed"+e.getMessage());
                        loginPBar.setVisibility(View.INVISIBLE);
                        wrongCredLogin.setVisibility(View.VISIBLE);
                        loginAttempts--;

                        if (loginAttempts==2){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loginAttempts=3;
                                }
                            }, WAIT_TIME);
                        }
                    }
                });
            }
        });

        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

        resetPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter your email to receive the password link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail=resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG,"Reset Link Sent to Your Email");
                                Toast.makeText(Login.this,"Reset Link Sent to Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Error! Reset Link is not Sent" + e.getMessage());
                                Toast.makeText(Login.this, "Error! Reset Link is not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
}